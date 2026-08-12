# Arch1 - 3티어 아키텍처 웹 서비스

## 프로젝트 개요

Arch1은 3티어 아키텍처를 기반으로 한 현대적인 웹 서비스입니다. 정적 페이지와 동적 페이지가 분리되어 있으며, 프론트엔드와 백엔드가 독립적으로 구성되어 있습니다.

## 아키텍처 구성

### 1. 프레젠테이션 계층 (Presentation Tier)
- **정적 페이지**: HTML, CSS, JavaScript (Tailwind CSS 사용)
  - 홈 페이지 (`/frontend/index.html`)
  - About 페이지 (`/frontend/about.html`)
- **동적 페이지**: Thymeleaf 템플릿 엔진
  - 로그인/로그아웃 페이지
  - 대시보드 페이지
  - 게시판 페이지

### 2. 비즈니스 로직 계층 (Business Logic Tier)
- **Spring Boot 3.x** 기반 애플리케이션 서버
- **Spring Security** - 사용자 인증 및 권한 관리
- **Spring Data JPA** - 데이터 접근 계층
- **Thymeleaf** - 서버 사이드 템플릿 엔진

### 3. 데이터 계층 (Data Tier)
- **H2 Database** (개발용, 메모리 DB)
- **JPA/Hibernate** - 객체 관계 매핑
- **MySQL/PostgreSQL** 지원 (설정 변경 가능)

## 기술 스택

### Frontend
- HTML5
- CSS3 (Tailwind CSS)
- JavaScript (ES6+)

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 Database
- Lombok

## 프로젝트 구조

```
arch1/
├── frontend/                 # 정적 페이지
│   ├── index.html           # 홈 페이지
│   ├── about.html           # About 페이지
│   ├── css/
│   │   └── style.css        # 커스텀 CSS
│   ├── js/
│   │   └── script.js        # 커스텀 JavaScript
│   └── images/              # 이미지 파일들
│
└── backend/                 # Spring Boot 애플리케이션
    ├── build.gradle         # Gradle 빌드 설정
    ├── src/main/java/
    │   └── com/arch1/webapp/
    │       ├── Arch1WebApplication.java    # 메인 애플리케이션
    │       ├── config/                     # 설정 클래스들
    │       ├── controller/                 # 컨트롤러
    │       ├── entity/                     # JPA 엔티티
    │       ├── repository/                 # 데이터 접근 계층
    │       └── service/                    # 비즈니스 로직
    └── src/main/resources/
        ├── application.yml   # 애플리케이션 설정
        ├── templates/        # Thymeleaf 템플릿
        └── static/           # 정적 리소스
```

## 주요 기능

### 정적 페이지
- **홈 페이지**: 3티어 아키텍처 소개 및 특징 설명
- **About 페이지**: 아키텍처 상세 설명 및 기술 스택 소개

### 동적 페이지
- **로그인/로그아웃**: 사용자 인증
- **회원가입**: 새 사용자 등록
- **대시보드**: 사용자별 통계 및 최근 게시글
- **게시판**: 게시글 목록, 작성, 조회, 수정, 삭제

## 실행 방법

### 1. 데이터베이스 설정
MySQL 데이터베이스가 필요합니다:

```bash
# MySQL 서버에 연결
mysql -h 192.168.11.100 -u admin -p

# 데이터베이스 초기화 스크립트 실행
source database/init.sql

# 연결 테스트
./database/test_connection.sh
```

**데이터베이스 정보:**
- Host: `192.168.11.100`
- Port: `3306`
- Database: `arch1db`
- Username: `admin`
- Password: `1qaz@WSX`

### 2. 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 3. 프론트엔드 접속
- 정적 페이지: `http://localhost:8080/backend/`
- 동적 페이지: `http://localhost:8080/backend/login`

### 4. H2 데이터베이스 콘솔 (개발용)
- URL: `http://localhost:8080/backend/h2-console`
- JDBC URL: `jdbc:h2:mem:arch1db`
- Username: `sa`
- Password: (비어있음)

## 초기 계정

애플리케이션 실행 시 자동으로 생성되는 초기 계정들:

### 관리자 계정
- Username: `admin`
- Password: `admin123`
- Role: `ADMIN`

### 일반 사용자 계정
- Username: `user1`
- Password: `user123`
- Role: `USER`

- Username: `user2`
- Password: `user123`
- Role: `USER`

## 개발 환경 설정

### 필수 요구사항
- Java 17 이상
- Gradle 7.x 이상

### IDE 설정
- IntelliJ IDEA 또는 Eclipse 권장
- Spring Boot 플러그인 설치
- Lombok 플러그인 설치

## 배포

### 개발 환경
- H2 메모리 데이터베이스 사용
- 개발용 설정으로 자동 구성

### 운영 환경
- `application.yml`에서 데이터베이스 설정 변경
- MySQL 또는 PostgreSQL 사용 권장
- 보안 설정 강화 필요

## 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 기여

프로젝트 개선을 위한 제안이나 버그 리포트는 언제든 환영합니다.
