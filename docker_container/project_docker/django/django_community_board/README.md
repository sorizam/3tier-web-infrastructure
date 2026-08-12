# Community Board
Django 를 사용하여 기초적인 3 tier 아키텍처 구성을 연습하기 위해 만들어진 프로젝트 입니다.

## 사용 방법

``` bash
git clone https://github.com/AnByoungHyun/django-community-board.git
cd django-community-board/
sudo apt-get install -y python3.10-venv mysql-client python3-dev \
  default-libmysqlclient-dev pkg-config libmysqlclient-dev \
  build-essential
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

## 데이터베이스 연결 구성

`community_board_project/settings.py` 파일을 vi 로 열기.

다음의 섹션을 찾아서 데이터베이스 정보를 기입
``` python3
DATABASES = {
    # "default": {
    #     "ENGINE": "django.db.backends.sqlite3",
    #     "NAME": BASE_DIR / "db.sqlite3",
    # }
    "default": {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': '데이터베이스명',
        'USER': '데이터베이스 계정',
        'PASSWORD': '데이터베이스 암호',
        'HOST': '데이터베이스 IP 주소',
        'PORT': '데이터베이스 Port 번호',
    }
}
```

``` bash
# 데이터베이스에 등록할 데이터베이스 구조 생성
python3 manage.py makemigrations

# 데이터베이스에 적용
python3 manage.py migrate

# Django 서버 실행
python3 manage.py runserver 0.0.0.0:8000
```













