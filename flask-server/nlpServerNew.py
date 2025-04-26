from flask import Flask, request, jsonify
from bs4 import BeautifulSoup
from google.cloud import language_v1
import os
import requests
from dotenv import load_dotenv  

load_dotenv()  
google_credential_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")

if google_credential_path:
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = google_credential_path
else:
    raise Exception("❌ GOOGLE_APPLICATION_CREDENTIALS가 설정되지 않았습니다.")

app = Flask(__name__)

@app.route("/analyze", methods=["POST"])
def analyze_text():
    data = request.get_json()
    url = data.get("text", "")  

    try:
        html = requests.get(url, timeout=5).text
        soup = BeautifulSoup(html, "html.parser")
        article_text = " ".join([p.get_text() for p in soup.find_all("p")])

        if not article_text.strip():
            print("본문 없음. 분석 생략")
            return jsonify({"error": "본문을 추출할 수 없습니다."}), 400

        client = language_v1.LanguageServiceClient()
        document = language_v1.Document(content=article_text, type_=language_v1.Document.Type.PLAIN_TEXT)
        response = client.analyze_entities(request={"document": document})

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

        score = avg_salience * 100 + entity_count * 0.1 + type_score * 0.5 + text_length * 0.001 + jargon_penalty

        if score > 60:
            difficulty = "고급"
        elif score > 30:
            difficulty = "중급"
        else:
            difficulty = "초급"

        print(f"✅ 분석 완료 | 점수: {score} | 난이도: {difficulty}", flush=True)

        return jsonify({
            "difficulty": difficulty,
            "entities": entities,
            "textLength": text_length
        })

    except Exception as e:
        print("❌ 에러:", str(e))
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=6000)