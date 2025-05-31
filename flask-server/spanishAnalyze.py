import os
import re
from flask import Flask, request, jsonify
from dotenv import load_dotenv
from google.cloud import language_v1
from pathlib import Path

# ✅ 현재 파일 기준으로 .env.dev 로드
BASE_DIR = Path(__file__).resolve().parent
load_dotenv(dotenv_path=BASE_DIR / ".env.dev")

# ✅ 환경 변수 확인 및 경로 설정
creds_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")

if not creds_path:
    raise EnvironmentError("❌ GOOGLE_APPLICATION_CREDENTIALS 환경변수가 설정되지 않았습니다.")
else:
    # 절대경로가 아닌 경우 BASE_DIR 기준으로 변환
    full_path = os.path.join(BASE_DIR, creds_path) if not os.path.isabs(creds_path) else creds_path
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = full_path

# ✅ Flask 앱 생성
app = Flask(__name__)
client = language_v1.LanguageServiceClient()

# ✅ 난이도 분류 기준 (Vásquez-Rodríguez et al., 2022 기반)
def classify_level(score):
    if score < 3.8:
        return "초급"
    elif score < 5.6:
        return "중급"
    else:
        return "고급"

# ✅ 본문 분석 함수
def analyze_spanish_text(text):
    # 문장 분리
    sentences = re.split(r'[.!?\n]+', text)
    sentences = [s.strip() for s in sentences if s.strip()]
    total_sentences = len(sentences) or 1
    total_chars = sum(len(s) for s in sentences)
    avg_sentence_length = total_chars / total_sentences

    document = language_v1.Document(content=text, type_=language_v1.Document.Type.PLAIN_TEXT, language="es")
    response = client.analyze_syntax(request={"document": document})

    counts = {
        "words": 0,
        "nouns": 0,
        "verbs": 0,
        "adjectives": 0,
        "conjunctions": 0
    }

    for token in response.tokens:
        tag = language_v1.PartOfSpeech.Tag(token.part_of_speech.tag).name
        counts["words"] += 1

        if tag == "NOUN":
            counts["nouns"] += 1
        elif tag == "VERB":
            counts["verbs"] += 1
        elif tag == "ADJ":
            counts["adjectives"] += 1
        elif tag == "CONJ":
            counts["conjunctions"] += 1

    total_words = counts["words"] or 1
    noun_ratio = counts["nouns"] / total_words
    verb_ratio = counts["verbs"] / total_words
    adj_ratio = counts["adjectives"] / total_words
    conj_ratio = counts["conjunctions"] / total_words

    # ✅ 논문 기반 난이도 점수 계산 (Vásquez-Rodríguez et al., 2022 기반 가중치)
    score = (
        0.18 * avg_sentence_length +
        0.3 * conj_ratio +
        0.25 * adj_ratio +
        0.2 * verb_ratio +
        0.1 * noun_ratio
    ) * 10  # 점수를 0~10 스케일로 정규화

    return {
        "score": round(score, 3),
        "level": classify_level(score),
        "sentence_len": round(avg_sentence_length, 2),
        "noun_ratio": round(noun_ratio, 2),
        "verb_ratio": round(verb_ratio, 2),
        "adj_ratio": round(adj_ratio, 2),
        "conj_ratio": round(conj_ratio, 2),
        "sentences_analyzed": total_sentences
    }

# ✅ 엔드포인트: POST로 기사 본문 받아서 분석
@app.route("/analyze-spanish-news", methods=["POST"])
def analyze_spanish_news():
    try:
        data = request.get_json()
        text = data.get("text", "")
        if not text.strip():
            return jsonify({"error": "텍스트가 비어 있습니다."}), 400

        analysis = analyze_spanish_text(text)
        return jsonify(analysis)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ✅ 실행
if __name__ == "__main__":
    app.run(debug=True, port=6400)

