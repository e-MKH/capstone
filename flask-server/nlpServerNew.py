from flask import Flask, request, jsonify
from google.cloud import language_v1
import os
import re
from dotenv import load_dotenv

# ✅ .env 파일에서 환경변수 로드 (GOOGLE_APPLICATION_CREDENTIALS 포함)
load_dotenv(dotenv_path=".env.nlp")
google_credential_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")

# ✅ 서비스 계정 키 환경 변수 설정
if google_credential_path:
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = google_credential_path
else:
    raise Exception("❌ GOOGLE_APPLICATION_CREDENTIALS가 설정되지 않았습니다.")

# ✅ Flask 앱 생성
app = Flask(__name__)

# ✅ 수동태 문장을 탐지하는 함수
def count_passive_sentences(text: str) -> int:
    # be 동사 + ed 형태로 이루어진 패턴을 통해 수동태 감지
    passive_patterns = [
        r"\b(is|are|was|were|be|been|being|has been|have been|had been|will be|would be|can be|could be|should be|may be|might be)\s+\w+ed\b"
    ]
    sentences = re.split(r'[.!?]', text)
    count = 0
    for sentence in sentences:
        for pattern in passive_patterns:
            if re.search(pattern, sentence.strip(), re.IGNORECASE):
                count += 1
                break
    return count

# ✅ NLP 분석 엔드포인트
@app.route("/analyze", methods=["POST"])
def analyze_text():
    data = request.get_json()
    article_text = data.get("text", "")

    try:
        if not article_text.strip():
            print("❌ 본문 없음. 분석 생략", flush=True)
            return jsonify({"error": "본문을 추출할 수 없습니다."}), 400

        # ✅ 문장 분석
        sentences = re.split(r'[.!?]', article_text)
        sentence_count = len([s for s in sentences if s.strip()])
        avg_sentence_length = len(article_text.split()) / sentence_count if sentence_count else 0

        # ✅ 접속사 개수 측정
        conjunctions = ["if", "although", "because", "while", "when", "that", "which", "who", "whose"]
        conjunction_count = sum(article_text.lower().count(conj) for conj in conjunctions)

        # ✅ 수동태 문장 수 측정
        passive_count = count_passive_sentences(article_text)
        passive_ratio = passive_count / sentence_count if sentence_count else 0

        # ✅ Google Cloud NLP 클라이언트 생성
        client = language_v1.LanguageServiceClient()
        document = language_v1.Document(content=article_text, type_=language_v1.Document.Type.PLAIN_TEXT)

        # ✅ 엔터티 분석 수행
        entity_response = client.analyze_entities(request={"document": document})
        entities = entity_response.entities
        avg_salience = sum(e.salience for e in entities) / len(entities) if entities else 0
        entity_count = len(entities)

        # ✅ 커스텀 점수 기반 난이도 계산
        score = (
            avg_salience * 100 +
            entity_count * 0.1 +
            conjunction_count * 0.3 +
            passive_ratio * 10 +
            (sentence_count * avg_sentence_length) * 0.01
        )

        # ✅ 점수에 따른 난이도 분류
        if score > 60:
            difficulty = "고급"
        elif score > 30:
            difficulty = "중급"
        else:
            difficulty = "초급"

        # ✅ 로그 출력
        print(
            f"✅ 분석 완료 | 점수: {score:.2f} | 난이도: {difficulty} | 문장수: {sentence_count} | "
            f"평균문장길이: {avg_sentence_length:.2f} | 접속사수: {conjunction_count} | 수동태문장수: {passive_count}",
            flush=True
        )

        # ✅ 결과 JSON 반환
        return jsonify({
            "difficulty": difficulty,
            "entities": [
                {
                    "name": e.name,
                    "type": language_v1.Entity.Type(e.type_).name,
                    "salience": e.salience
                } for e in entities
            ],
            "textLength": len(article_text)
        })

    except Exception as e:
        print("❌ 에러:", str(e), flush=True)
        return jsonify({"error": str(e)}), 500

# ✅ 서버 실행
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=6000)
