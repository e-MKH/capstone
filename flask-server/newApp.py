from flask import Flask, request, jsonify
import trafilatura

app = Flask(__name__)

@app.route("/extract", methods=["POST"])
def extract_article():
    data = request.json
    url = data.get("url")

    try:
        print("⏳ URL 받음:", url, flush=True)

        # 1. HTML 다운로드
        downloaded = trafilatura.fetch_url(url)
        if not downloaded:
            print("❌ 다운로드 실패", flush=True)
            return jsonify({"error": "본문을 가져오지 못했습니다."}), 400

        # 2. 본문 추출
        text = trafilatura.extract(downloaded)
        if not text:
            print("❌ 파싱 실패", flush=True)
            return jsonify({"error": "본문 파싱 실패"}), 400

        print(f"✅ 본문 추출 완료 | 길이: {len(text)}", flush=True)
        print("📝 본문 앞부분:", text[:300], flush=True)

        # 3. 본문 길이 제한
        if len(text) < 200:
            print("⚠️ 본문이 너무 짧음", flush=True)
            return jsonify({"error": "본문이 너무 짧습니다."}), 400

        return jsonify({
            "url": url,
            "text": text
        })

    except Exception as e:
        print("❌ 예외 발생:", str(e), flush=True)
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)


