# News Flask Server

GNews API를 통해 가져온 뉴스 기사의 본문을 추출하고,  
Google Cloud API를 활용해 기사 난이도 분석 및 번역을 지원하는 Flask 서버입니다.

---

## 설치 방법

### 1. 가상환경(venv) 생성 및 활성화
```bash
# (생성은 이미 되어 있다면 생략)
# Windows
venv\Scripts\activate

# Mac/Linux
source venv/bin/activate

2. 필수 라이브러리 설치
pip install -r requirements.txt

환경 변수 설정
Google Cloud 서비스 계정 키 파일(JSON)을 준비합니다.

.env.nlp, .env.translate 파일을 프로젝트 루트에 생성합니다.

.env.nlp 예시:
GOOGLE_APPLICATION_CREDENTIALS=your-nlp-credentials.json

.env.translate 예시:
GOOGLE_APPLICATION_CREDENTIALS=your-translate-credentials.json

서버 실행 방법
서버는 세 부분으로 나뉘어져 있습니다. 각각 별도의 포트로 실행해야 합니다.

1. 기사 본문 추출 서버 (port 5000)
URL을 받아 기사 본문을 추출합니다.
python newApp.py

2. 기사 난이도 분석 서버 (port 6000)
본문 텍스트를 받아 엔터티 분석 후 난이도를 판별합니다.
python nlpServerNew.py

3. 기사 번역 서버 (port 7000)
본문 텍스트를 받아 번역 결과를 반환합니다.
python translate.py

주의사항
각 서버(newApp.py, nlpServerNew.py, translate.py)는 서로 다른 포트(5000/6000/7000) 에서 독립적으로 실행해야 합니다.

Google Cloud 서비스 계정 키 파일이 정확히 설정되어 있어야 합니다.