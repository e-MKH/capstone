# Newsdata.io API로 스페인어 기사 수집하기

import requests
from dotenv import load_dotenv
import os

# ✅ 절대경로로 명시
env_path = os.path.join(os.path.dirname(__file__), '.env.dev')
load_dotenv(dotenv_path=env_path)

API_KEY = os.getenv("NEWSDATA_API_KEY")
print("🔐 API KEY:", API_KEY)  # ✅ API 키 정상 출력 확인

def fetch_news(category: str, language: str = "es"):
    url = "https://newsdata.io/api/1/news"
    params = {
        "apikey": API_KEY,
        "language": language,
        "category": category
    }

    response = requests.get(url, params=params)
    print("📡 Status Code:", response.status_code)

    if response.status_code != 200:
        print("❌ API 요청 실패! 응답 본문 ↓")
        print(response.text)
        return [{"title": "Error", "description": "API 호출 실패", "link": ""}]

    try:
        data = response.json()
    except Exception as e:
        print("❌ JSON 파싱 실패:", e)
        print("📄 응답 본문:", response.text[:500])
        return [{"title": "Error", "description": "JSON 파싱 실패", "link": ""}]

    # ✅ 응답 구조 확인용 출력
    print("📦 전체 응답 구조:", data)
    print("🧪 결과 타입:", type(data.get("results")))
    print("🧪 첫 item 타입:", type(data.get("results", [])[0]) if data.get("results") else "No results")

    articles = []
    for item in data.get("results", []):
        if isinstance(item, dict):
            articles.append({
                "title": item.get("title"),
                "link": item.get("link"),
                "description": item.get("description"),
                "content": item.get("content")  # ⚠️ 일부 기사에 content 필드 있음
            })

    return articles


# ✅ 직접 실행할 때 테스트
if __name__ == "__main__":
    test_articles = fetch_news("science")
    print("📋 수집된 기사 수:", len(test_articles))
    print("🔖 예시 기사:", test_articles[0] if test_articles else "없음")
