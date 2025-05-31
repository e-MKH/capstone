import os
import requests
from dotenv import load_dotenv
from bs4 import BeautifulSoup
from spanishAnalyze import analyze_spanish_text

# ✅ 환경 변수 로딩
env_path = os.path.join(os.path.dirname(__file__), '.env.dev')
load_dotenv(dotenv_path=env_path)

GNEWS_API_KEY = os.getenv("GNEWS_API_KEY")

# ✅ GNews API 기사 수집
def fetch_news(category: str, language: str = "es", country: str = "es", max_results: int = 5):
    url = "https://gnews.io/api/v4/top-headlines"
    params = {
        "apikey": GNEWS_API_KEY,
        "lang": language,
        "topic": category,
        "country": country,
        "max": max_results
    }
    response = requests.get(url, params=params)
    if response.status_code != 200:
        print("API 요청 실패:", response.text)
        return []
    return response.json().get("articles", [])

# ✅ 기사 본문 크롤링 함수
def extract_article_body(url: str) -> str:
    try:
        res = requests.get(url, timeout=10)
        soup = BeautifulSoup(res.text, "html.parser")

        # 대표적인 본문 선택자 시도 (사이트마다 다름 → 조정 필요)
        candidates = [
            {"tag": "article"},  # 기본
            {"tag": "div", "class_": "article-body"},
            {"tag": "div", "class_": "content"},
            {"tag": "div", "class_": "entry-content"},
        ]

        for c in candidates:
            tag = c.get("tag")
            class_ = c.get("class_")
            if class_:
                target = soup.find(tag, class_=class_)
            else:
                target = soup.find(tag)

            if target:
                texts = target.stripped_strings
                full_text = " ".join(texts)
                if len(full_text) >= 300:
                    return full_text
        return ""
    except Exception as e:
        print(f"크롤링 실패: {url}\n에러: {e}")
        return ""

# ✅ 전체 수집 → 크롤링 → 분석
def fetch_and_analyze_all():
    categories = ["politics", "business", "science", "technology", "world"]
    for category in categories:
        print(f"\n카테고리: {category}")
        articles = fetch_news(category=category)
        for article in articles:
            url = article.get("url")
            title = article.get("title")
            print(f"\n기사 제목: {title}")
            print(f"URL: {url}")

            content = extract_article_body(url)
            if not content:
                print("본문 수집 실패 또는 너무 짧음.")
                continue

            result = analyze_spanish_text(content)
            print("분석 결과:", result)

# ✅ 테스트 실행
if __name__ == "__main__":
    fetch_and_analyze_all()