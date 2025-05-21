import os
from flask import Flask, request, jsonify
import requests
import re
import fugashi
from dotenv import load_dotenv

# ✅ .env에서 GNews API 키 불러오기
load_dotenv(dotenv_path=".env.japanese")
GNEWS_API_KEY = os.getenv("GNEWS_API_KEY")

# ✅ Flask 앱 생성
app = Flask(__name__)
tagger = fugashi.Tagger()

# ✅ 난이도 등급 분류 기준
def classify_level(score):
    if score >= 10.5:
        return '하 (易)'
    elif score >= 9.5:
        return '중 (中)'
    else:
        return '상 (難)'

# ✅ 일본어 기사 텍스트 난이도 분석
def analyze_japanese(text):
    sentences = re.split(r'(?<=[。！？])', text)
    total_chars = sum(len(s) for s in sentences)
    total_sentences = len(sentences) or 1

    counts = {'kanji_words': 0, 'wago_words': 0, 'verbs': 0, 'aux_verbs': 0, 'words': 0}

    for sent in sentences:
        for word in tagger(sent):
            surface = word.surface
            pos = word.feature.pos
            counts['words'] += 1
            if re.search(r'[一-龥]', surface):
                counts['kanji_words'] += 1
            elif re.fullmatch(r'[ぁ-んー]+', surface):
                counts['wago_words'] += 1
            if pos == '動詞':
                counts['verbs'] += 1
            if pos == '助動詞':
                counts['aux_verbs'] += 1

    avg_sent_len = total_chars / total_sentences
    total_words = counts['words'] or 1

    p_kanji = counts['kanji_words'] / total_words
    p_wago = counts['wago_words'] / total_words
    p_verb = counts['verbs'] / total_words
    p_aux = counts['aux_verbs'] / total_words

    score = (-0.056 * avg_sent_len
             -0.126 * p_kanji
             -0.042 * p_wago
             -0.145 * p_verb
             -0.044 * p_aux
             + 11.724)

    return {
        'score': round(score, 3),
        'level': classify_level(score),
        'sentence_len': round(avg_sent_len, 2),
        'kanji_ratio': round(p_kanji, 2),
        'wago_ratio': round(p_wago, 2),
        'verb_ratio': round(p_verb, 2),
        'particle_ratio': round(p_aux, 2),
        'sentences_analyzed': total_sentences
    }

# ✅ GNews API로 일본어 기사 가져오기
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
    return [a["title"] + " " + a.get("description", "") for a in articles]

# ✅ 엔드포인트: /analyze-japanese-news?q=정치
@app.route("/analyze-japanese-news", methods=["GET"])
def analyze_japanese_news():
    keyword = request.args.get("q", "経済")
    try:
        articles = fetch_japanese_articles(keyword)
        results = []
        for text in articles:
            analysis = analyze_japanese(text)
            results.append({
                "original": text[:100] + "...",
                "analysis": analysis
            })
        return jsonify({
            "keyword": keyword,
            "results": results
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ✅ 서버 실행 (port=6100)
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=6100)
