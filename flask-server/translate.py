import os
os.environ["FLASK_ENV"] = "development"
from flask import Flask, request, jsonify
from google.cloud import translate_v2 as translate
import os
from dotenv import load_dotenv


load_dotenv(dotenv_path=".env.translate")

google_credential_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")

if google_credential_path:
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = google_credential_path
else:
    raise Exception("❌ GOOGLE_APPLICATION_CREDENTIALS가 설정되지 않았습니다.")

app = Flask(__name__)


translator = translate.Client()

@app.route("/translate", methods=["POST"])
def translate_text():
    data = request.get_json()
    text = data.get("text", "")
    target_language = data.get("target_language", "ko")  

    try:
        result = translator.translate(text, target_language=target_language)
        translated_text = result["translatedText"]
        return jsonify({"translated_text": translated_text})
    except Exception as e:
        print("❌ 번역 에러:", str(e))
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=7000)