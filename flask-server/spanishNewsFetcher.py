import os
import requests
from dotenv import load_dotenv
from fetchSpanishNews import fetch_news

# ✅ 환경 변수 로딩 (.env.dev)
env_path = os.path.join(os.path.dirname(__file__), '.env.dev')
load_dotenv(dotenv_path=env_path)

ANALYZE_API_URL = "http://localhost:6400/analyze-spanish-news"  # Flask 서버 주소

# ✅ 카테고리 목록 (Newsdata.io 기준)
CATEGORIES = ["politics", "business", "science", "technology", "world"]

def analyze_article(article):
    content = article.get("content", "")
    if not content or len(content.strip()) < 30:
        print("⚠️ 본문 없음 또는 너무 짧음 → 스킵")
        return None

    try:
        response = requests.post(
            ANALYZE_API_URL,
            json={"text": content},
            timeout=10
        )
        if response.status_code != 200:
            print("❌ 분석 실패:", response.text)
            return None
        result = response.json()
        return {
            "title": article.get("title"),
            "link": article.get("link"),
            "category": article.get("category"),
            "difficulty": result.get("level"),
            "score": result.get("score")
        }
    except Exception as e:
        print("❌ 요청 중 오류:", e)
        return None

# ✅ 통합 실행
def fetch_and_analyze_all():
    all_results = []

    for category in CATEGORIES:
        print(f"\n📰 카테고리 '{category}' 기사 수집 중...")
        articles = fetch_news(category=category, language="es")

        for article in articles:
            article["category"] = category  # 카테고리 직접 추가
            result = analyze_article(article)
            if result:
                all_results.append(result)
                print(f"✅ [{result['difficulty']}] {result['title']}")

    print(f"\n📊 전체 분석 완료! 총 {len(all_results)}건")
    return all_results

# ✅ 테스트 실행
if __name__ == "__main__":
    fetch_and_analyze_all()
