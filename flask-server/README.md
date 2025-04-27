# News NLP Flask Server

## 설치 방법

1. **가상환경(venv) 활성화**
   ```bash
   # Windows
   venv\Scripts\activate

   # Mac/Linux
   source venv/bin/activate

2. 필수 라이브러리 설치
pip install -r requirements.txt

3. 서버 실행 방법
python newApp.py
python nlpServerNew.py

<환경 변수 설정>
GOOGLE_APPLICATION_CREDENTIALS=your-credentials-file.json
(your-credentials-file.json 부분은 본인의 Google Cloud 서비스 계정 키 파일 이름으로 변경하세요.)