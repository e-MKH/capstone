from flask import Flask, request, jsonify
from bs4 import BeautifulSoup
from google.cloud import language_v1
import os
import requests

# ✅ 서비스 계정 키 등록
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "news-nlp-analyzer-6e707fe7162a.json"

app = Flask(__name__)

@app.route("/analyze", methods=["POST"])
def analyze_text():
    data = request.get_json()
    url = data.get("text", "")  # URL이 "text" 필드로 들어옴

    try:
        # ✅ 기사 본문 가져오기
        html = requests.get(url, timeout=5).text
        soup = BeautifulSoup(html, "html.parser")
        article_text = " ".join([p.get_text() for p in soup.find_all("p")])

        if not article_text.strip():
            print("본문 없음. 분석 생략")
            return jsonify({"error": "본문을 추출할 수 없습니다."}), 400

        # ✅ NLP 분석 시작
        client = language_v1.LanguageServiceClient()
        document = language_v1.Document(content=article_text, type_=language_v1.Document.Type.PLAIN_TEXT)
        response = client.analyze_entities(request={"document": document})
        


        # ✅ 엔터티 분석
        entities = [
            {
                "name": entity.name,
                "type": language_v1.Entity.Type(entity.type_).name,
                "salience": entity.salience,
                "wikipedia_url": entity.metadata.get("wikipedia_url", "")
            }
            for entity in response.entities
        ]

        avg_salience = sum(e["salience"] for e in entities) / len(entities) if entities else 0
        entity_count = len(entities)
        text_length = len(article_text)
        type_score = sum(
            2 for e in entities if e["type"] in ["PERSON", "ORGANIZATION", "LOCATION", "WORK_OF_ART"]
        )
        jargon_penalty = sum(-0.2 for e in entities if "wikipedia_url" not in e)

        # ✅ 점수 계산 (3단계 분류용)
        score = avg_salience * 100 + entity_count * 0.1 + type_score * 0.5 + text_length * 0.001 + jargon_penalty

        if score > 60:
            difficulty = "고급"
        elif score > 30:
            difficulty = "중급"
        else:
            difficulty = "초급"


        print(f"✅ 분석 완료 | 점수: {score} | 난이도: {difficulty}", flush=True)


        print(f"✅ 난이도: {difficulty} | 평균 중요도: {avg_salience:.4f} | 엔터티: {entity_count} | 길이: {text_length} | 전문용어 패널티: {jargon_penalty:.2f} | 점수: {score:.2f}")

        return jsonify({
            "difficulty": difficulty,
            "entities": entities,
            "textLength": len(article_text)
        })

    except Exception as e:
        print("❌ 에러:", str(e))
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=6000)