from flask import Flask, request, jsonify
import os
from jpDifficultyAnalyzer import analyze_article
import json

app = Flask(__name__)

# GnewsAPI API 키와 쿼리 설정
API_KEY = "your_gnews_api_key"  # GnewsAPI에서 받은 API 키

# JLPT 단어 리스트 파일 경로
JLPT_WORDS_FILE = "flask_server/jlpt_words.json"

@app.route('/analyze', methods=['POST'])
def analyze():
    # POST로 전달된 JSON에서 'query' 파라미터를 추출
    data = request.get_json()
    query = data.get('query', '')

    if not query:
        return jsonify({"error": "Query parameter is missing"}), 400

    # 기사를 분석하고 난이도를 반환
    difficulty_level = analyze_article(API_KEY, query, JLPT_WORDS_FILE)

    if difficulty_level:
        return jsonify({"difficulty": difficulty_level}), 200
    else:
        return jsonify({"error": "No article found or analysis failed"}), 500

if __name__ == "__main__":
    app.run(debug=True)
