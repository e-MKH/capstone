import os
import re
from flask import Flask, request, jsonify
import requests
from dotenv import load_dotenv
from google.cloud import language_v1

# ✅ 환경 변수 불러오기 (.env.nlp 사용)
load_dotenv(dotenv_path=".env.nlp")
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")
GNEWS_API_KEY = os.getenv("GNEWS_API_KEY")

# ✅ Flask 앱 초기화
app = Flask(__name__)
client = language_v1.LanguageServiceClient()

# ✅ 난이도 등급 분류 기준
def classify_level(score):
    if score >= 10.5:
        return "하 (易)"
    elif score >= 9.5:
        return "중 (中)"
    else:
        return "상 (難)"

# ✅ 텍스트 분석 함수
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

# ✅ 기사 수집 함수 (url 포함)
def fetch_japanese_articles(keyword):
    url = "https://gnews.io/api/v4/search"
    params = {
        "q": keyword,
        "lang": "ja",
        "country": "jp",
        "token": GNEWS_API_KEY,
        "max": 3
    }
    res = requests.get(url, params=params)
    articles = res.json().get("articles", [])
    return [{
        "text": a["title"] + " " + a.get("description", ""),
        "url": a.get("url", "")
    } for a in articles]

# ✅ 라우트 정의
@app.route("/analyze-japanese-news", methods=["GET"])
def analyze_japanese_news():
    keyword = request.args.get("q", "経済")
    try:
        articles = fetch_japanese_articles(keyword)
        results = []

        for article in articles:
            analysis = analyze_text(article["text"])
            results.append({
                "original": article["text"][:100] + "...",
                "url": article["url"],
                "analysis": analysis
            })

        return jsonify({
            "keyword": keyword,
            "results": results
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ✅ 실행
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=6100)