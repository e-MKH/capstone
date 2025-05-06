from flask import Flask, jsonify, request
import requests
from bs4 import BeautifulSoup
import time
import re

app = Flask(__name__)

@app.route('/easy-news', methods=['GET'])
def get_easy_news():
    start = time.time()
    print("📥 /easy-news 요청 수신됨")

    base_url = "https://www.newsinlevels.com/level/level-1/"
    response = requests.get(base_url)
    print("🌐 레벨1 페이지 요청 완료")

    soup = BeautifulSoup(response.text, "html.parser")
    articles = []

    # 1. 링크만 수집
    for a_tag in soup.select("h3 > a[href*='products']"):
        article_url = a_tag['href']
        print("🔗 기사 링크 발견:", article_url)

        # 2. Flask 서버에 본문 추출 요청
        try:
            extract_response = requests.post("http://localhost:5000/extract", json={"url": article_url})
            if extract_response.status_code == 200:
                result = extract_response.json()
                text = result.get("text", "")
                if len(text) > 0:
                    title = a_tag.get_text(strip=True)

                    # 게시일 추출 (예: 05-05-2025 15:00)
                    date_match = re.search(r"\d{2}-\d{2}-\d{4} \d{2}:\d{2}", text)
                    published_at = date_match.group() if date_match else ""

                    articles.append({
                        "title": title,
                        "content": text,
                        "url": article_url,
                        "publishedAt": published_at
                    })
                    print("✅ 본문 추출 성공:", title)
            else:
                print("⚠️ 본문 추출 실패 상태코드:", extract_response.status_code)
        except Exception as e:
            print("❌ 요청 중 오류 발생:", str(e))

        if len(articles) >= 5:
            break

    print(f"📦 최종 수집된 기사 수: {len(articles)}")
    print(f"⏱ 처리 시간: {time.time() - start:.2f}초")
    return jsonify(articles)


@app.route("/extract", methods=["POST"])
def extract_article():
    data = request.json
    url = data.get("url")

    try:
        print("⏳ URL 받음:", url)
        page = requests.get(url)
        soup = BeautifulSoup(page.text, "html.parser")

        # 기사 본문 추출
        content_div = soup.find("div", class_="entry-content")
        text = content_div.get_text(separator="\n", strip=True) if content_div else ""

        print(f"✅ 본문 추출 완료 | 길이: {len(text)}")
        print("📝 본문 앞부분:", text[:200])
        return jsonify({"text": text})
    except Exception as e:
        print("❌ 본문 추출 중 오류:", str(e))
        return jsonify({"error": "본문 추출 실패"}), 500


if __name__ == "__main__":
    print("✅ 서버 시작됨: http://localhost:5001")
    app.run(debug=True, port=5001)