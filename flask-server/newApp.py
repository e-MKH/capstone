from flask import Flask, request, jsonify
import trafilatura

# Flask 앱 초기화
app = Flask(__name__)

# 모든 요청 전에 요청 메서드와 경로를 출력 (로깅 용도)
@app.before_request
def log_request():
    print(f"🌐 요청 수신됨: {request.method} {request.path}", flush=True)

# 기사 본문 추출 API 엔드포인트
@app.route("/extract", methods=["POST"])
def extract_article():
    data = request.json
    url = data.get("url")  # 클라이언트가 보낸 URL

    try:
        print("⏳ URL 받음:", url, flush=True)

        # 1. HTML 다운로드 시도
        downloaded = trafilatura.fetch_url(url)
        if not downloaded:
            print("❌ 다운로드 실패", flush=True)
            return jsonify({"error": "본문을 가져오지 못했습니다."}), 400

        # 2. HTML에서 텍스트 추출
        text = trafilatura.extract(downloaded)
        if not text:
            print("❌ 파싱 실패", flush=True)
            return jsonify({"error": "본문 파싱 실패"}), 400

        # 3. 본문 길이가 너무 짧은 경우 거부
        if len(text) < 200:
            print("⚠️ 본문이 너무 짧음", flush=True)
            return jsonify({"error": "본문이 너무 짧습니다."}), 400

        # 4. 학습용 꼬리말 제거
        cut_keywords = [
            "3000 WORDS WITH NEWS IN LEVEL",
            "words:",
            "can watch the original video",
            "Go to Level"
        ]
        for keyword in cut_keywords:
            if keyword in text:
                text = text.split(keyword)[0].strip()
                break

        # 본문 추출 성공 시 출력 로그
        print(f"✅ 본문 추출 완료 | 길이: {len(text)}", flush=True)
        print("📝 본문 앞부분:", text[:300], flush=True)

        # 성공적으로 본문을 추출하여 반환
        return jsonify({
            "url": url,
            "text": text
        })

    except Exception as e:
        print("❌ 예외 발생:", str(e), flush=True)
        return jsonify({"error": str(e)}), 500

# 서버 실행
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
