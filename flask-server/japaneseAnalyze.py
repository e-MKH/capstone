import os
import re
from flask import Flask, request, jsonify
from dotenv import load_dotenv
from google.cloud import language_v1
from fetchJapaneseNews import fetch_news  # ✅ 외부 함수로 뉴스 수집

# ✅ 환경 변수 로딩
load_dotenv(dotenv_path=".env.japanese")
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")

app = Flask(__name__)
client = language_v1.LanguageServiceClient()

# ✅ 일본어 및 영어 → GNews 카테고리 키 매핑
category_mapping = {
    # 일본어 키
    "政治": "politics",
    "経済": "business",
    "科学": "science",
    "技術": "technology",
    "スポーツ": "sports",
    "健康": "health",
    "エンタメ": "entertainment",
    "国際": "world",

    # 영어 키 (직접 요청 방지용)
    "politics": "politics",
    "business": "business",
    "science": "science",
    "technology": "technology",
    "sports": "sports",
    "health": "health",
    "entertainment": "entertainment",
    "world": "world"
}

# ✅ 난이도 분류 기준
def classify_level(score):
    if score >= 9.0:
        return "초급"
    elif score >= 8.5:
        return "중급"
    else:
        return "고급"

# ✅ 형태소 분석 + 난이도 계산
def analyze_text(text):
    sentences = re.split(r'(?<=[。！？])', text)
    total_chars = sum(len(s) for s in sentences)
    total_sentences = len(sentences) or 1
    avg_sentence_length = total_chars / total_sentences

    document = language_v1.Document(content=text, type_=language_v1.Document.Type.PLAIN_TEXT, language="ja")
    response = client.analyze_syntax(request={"document": document})

    counts = {
        "words": 0,
        "kanji_words": 0,
        "wago_words": 0,
        "verbs": 0,
        "aux_verbs": 0
    }

    for token in response.tokens:
        surface = token.text.content
        tag = language_v1.PartOfSpeech.Tag(token.part_of_speech.tag).name
        counts["words"] += 1

        if re.search(r"[一-龥]", surface):
            counts["kanji_words"] += 1
        elif re.fullmatch(r"[ぁ-んー]+", surface):
            counts["wago_words"] += 1

        if tag == "VERB":
            counts["verbs"] += 1
        elif tag == "AUXILIARY":
            counts["aux_verbs"] += 1

    total_words = counts["words"] or 1
    p_kanji = counts["kanji_words"] / total_words
    p_wago = counts["wago_words"] / total_words
    p_verb = counts["verbs"] / total_words
    p_aux = counts["aux_verbs"] / total_words

    score = (
        -0.056 * avg_sentence_length
        -0.126 * p_kanji
        -0.042 * p_wago
        -0.145 * p_verb
        -0.044 * p_aux
        + 11.724
    )

    return {
        "score": round(score, 3),
        "level": classify_level(score),
        "sentence_len": round(avg_sentence_length, 2),
        "kanji_ratio": round(p_kanji, 2),
        "wago_ratio": round(p_wago, 2),
        "verb_ratio": round(p_verb, 2),
        "particle_ratio": round(p_aux, 2),
        "sentences_analyzed": total_sentences
    }

# ✅ 통합된 GET/POST 라우트
@app.route("/analyze-japanese-news", methods=["GET", "POST"])
def analyze_japanese_news():
    try:
        if request.method == "POST":
            data = request.get_json()
            text = data.get("text", "")
            if not text.strip():
                return jsonify({"error": "텍스트가 비어 있습니다."}), 400

            print(f"📩 단일 기사 분석 요청 수신됨. 길이: {len(text)}자")
            analysis = analyze_text(text)
            print(f"✅ 분석 완료 → 점수: {analysis['score']}, 레벨: {analysis['level']}")
            return jsonify(analysis)

        else:  # GET 방식: category별 뉴스 분석
            original = request.args.get("category", "政治")
            category = category_mapping.get(original, "general")
            print(f"🔍 카테고리 분석 요청: {original} → {category}")

            articles = fetch_news(category)
            print(f"📰 가져온 기사 수: {len(articles)}")

            results = []
            for article in articles:
                title = article.get("title", "")
                desc = article.get("description", "")
                combined_text = f"{title}\n{desc}".strip()

                if not combined_text:
                    print("⚠️ 빈 기사 스킵됨")
                    continue

                analysis = analyze_text(combined_text)
                print(f"✅ 분석 완료: {title[:30]}... → 점수: {analysis['score']}, 레벨: {analysis['level']}")

                results.append({
                    "original": title,
                    "url": article.get("link", ""),
                    "description": desc,
                    "publishedAt": article.get("publishedAt", ""),
                    "analysis": analysis
                })

            return jsonify({
                "keyword": original,
                "results": results
            })

    except Exception as e:
        print(f"❌ 예외 발생: {str(e)}")
        return jsonify({"error": str(e)}), 500

# ✅ 실행
if __name__ == "__main__":
    app.run(debug=True, port=6100)
