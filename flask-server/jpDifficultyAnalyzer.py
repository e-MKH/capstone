import os
import requests
import json
from google.cloud import language_v1
from google.cloud.language_v1 import enums

# Google Cloud 인증 환경 변수 설정 (API 키)
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "path_to_your_service_account_key.json"

# JLPT 단어 리스트 로드 (json 파일에서 읽기)
def load_jlpt_words(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        jlpt_words = json.load(file)
    return jlpt_words

# JLPT 단어 레벨 반환 함수
def get_word_level(word, jlpt_words):
    for level, words in jlpt_words.items():
        if word in words:
            return level
    return None  # JLPT에 해당하지 않는 단어는 None으로 처리

# GnewsAPI에서 기사 불러오기
def fetch_article_from_gnews(api_key, query):
    url = f"https://gnews.io/api/v4/search?q={query}&token={api_key}"
    response = requests.get(url)
    if response.status_code == 200:
        articles = response.json().get('articles', [])
        if articles:
            return articles[0]['content']  # 첫 번째 기사 내용 가져오기
    return None

# 텍스트 분석 함수
def analyze_text(text, jlpt_words):
    client = language_v1.LanguageServiceClient()

    # 분석할 텍스트 설정
    document = language_v1.Document(content=text, type_=enums.Document.Type.PLAIN_TEXT, language="ja")

    # 문법 및 형태소 분석
    response = client.analyze_syntax(document=document)

    # 난이도 점수 초기화
    total_words = 0
    n5_words = 0
    n4_words = 0
    n3_words = 0
    n2_words = 0
    n1_words = 0

    # 형태소 분석 결과 처리
    for token in response.tokens:
        word = token.text.content
        word_level = get_word_level(word, jlpt_words)

        if word_level == "N5":
            n5_words += 1
        elif word_level == "N4":
            n4_words += 1
        elif word_level == "N3":
            n3_words += 1
        elif word_level == "N2":
            n2_words += 1
        elif word_level == "N1":
            n1_words += 1
        
        if word_level is not None:
            total_words += 1  # JLPT 단어가 있을 때만 계산

    # 난이도 점수 계산
    if total_words == 0:
        return "N/A"  # 난이도를 판별할 수 없는 경우

    n5_percentage = (n5_words / total_words) * 100
    n4_percentage = (n4_words / total_words) * 100
    n3_percentage = (n3_words / total_words) * 100
    n2_percentage = (n2_words / total_words) * 100
    n1_percentage = (n1_words / total_words) * 100

    # 난이도 결정 (단어 비율에 따라)
    if n1_percentage > 50:
        return "N1"
    elif n2_percentage > 50:
        return "N2"
    elif n3_percentage > 50:
        return "N3"
    elif n4_percentage > 50:
        return "N4"
    else:
        return "N5"  # N5가 대부분이면 N5로 반환

# 기사 분석 함수
def analyze_article(api_key, query, jlpt_words_file):
    # JLPT 단어 리스트 로드
    jlpt_words = load_jlpt_words(jlpt_words_file)

    # GnewsAPI에서 기사 불러오기
    article_text = fetch_article_from_gnews(api_key, query)
    
    if article_text:
        # 텍스트 분석하여 난이도 반환
        difficulty_level = analyze_text(article_text, jlpt_words)
        return difficulty_level
    else:
        return None
