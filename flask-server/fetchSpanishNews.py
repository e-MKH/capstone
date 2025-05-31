import requests
import os
from dotenv import load_dotenv

# ✅ 환경 변수 로딩 (.env.dev 사용)
env_path = os.path.join(os.path.dirname(__file__), '.env.dev')
load_dotenv(dotenv_path=env_path)

GNEWS_API_KEY = os.getenv("GNEWS_API_KEY")

def fetch_news(category: str, language: str = "es", country: str = "es", max_results: int = 10):
    url = "https://gnews.io/api/v4/top-headlines"
    params = {
        "apikey": GNEWS_API_KEY,
        "lang": language,
        "topic": category,
        "country": country,
        "max": max_results
    }

    response = requests.get(url, params=params)
    print("📡 상태 코드:", response.status_code)

    if response.status_code != 200:
        print("❌ API 요청 실패:", response.text)
        return [{"title": "에러 발생", "description": "API 호출 실패", "url": ""}]

    try:
        data = response.json()
    except Exception as e:
        print("❌ JSON 파싱 실패:", e)
        return [{"title": "에러 발생", "description": "JSON 파싱 실패", "url": ""}]

    articles = []
    for item in data.get("articles", []):
        articles.append({
            "title": item.get("title"),
            "description": item.get("description"),
            "url": item.get("url"),
            "publishedAt": item.get("publishedAt")
        })

    return articles


# ✅ 직접 실행 시 테스트
if __name__ == "__main__":
    test_category = "business"
    test_articles = fetch_news(test_category)
    print("📋 수집된 기사 수:", len(test_articles))
    print("🔖 예시 기사:", test_articles[0] if test_articles else "없음")
