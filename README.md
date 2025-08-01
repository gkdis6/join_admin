# Join Admin - 회원 관리 시스템

Spring Boot 기반의 회원 관리 및 관리자 시스템 API입니다.

## 🚀 빠른 시작

### 필요한 도구
- Docker & Docker Compose
- Git

### 실행하기
```bash
# 1. 프로젝트 클론
git clone <repository-url>
cd join_admin

# 2. 서비스 시작
docker-compose up -d --build

# 3. 브라우저에서 접속
# - 애플리케이션: http://localhost:8080
# - API Swagger 페이지: http://localhost:8080/swagger-ui/index.html
```

## 📋 구현된 요구사항

### 1️⃣ 회원가입 API
- **기능**: 계정/암호/성명/주민등록번호/핸드폰번호/주소 입력으로 회원가입
- **엔드포인트**: `POST /api/users/register`
- **중복 검증**: 계정, 주민등록번호 유일성 보장

### 2️⃣ 시스템 관리자 API (Basic Auth: admin/1212)
- **회원 조회**: `GET /api/admin/users` (페이징 지원)
- **회원 상세조회**: `GET /api/admin/users/{id}`
- **회원 수정**: `PUT /api/admin/users/{id}` (암호, 주소만 수정 가능)
- **회원 삭제**: `DELETE /api/admin/users/{id}`

### 3️⃣ 사용자 로그인 API
- **기능**: 회원가입한 사용자 로그인 (JWT 토큰 발급)
- **엔드포인트**: `POST /api/users/login`

### 4️⃣ 본인 정보 조회 API (JWT 토큰 필요)
- **기능**: 로그인한 사용자 본인 정보 조회
- **엔드포인트**: `GET /api/users/me`
- **특징**: 주소는 최상위 행정구역만 표시 (예: "서울특별시", "경기도")

### 5️⃣ 연령대별 메시지 발송 API (Basic Auth 필요)
- **기능**: 연령대별 카카오톡 메시지 발송 (실패시 SMS 대체)
- **엔드포인트**: `POST /api/admin/messages`
- **메시지 형식**: "{이름}님, 안녕하세요. 현대 오토에버입니다."
- **속도 제한**: 카카오톡 100회/분, SMS 500회/분

## 🛠 기술 스택

| 분야 | 기술 |
|-----|-----|
| Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Database | PostgreSQL |
| Security | Spring Security + JWT |
| Documentation | Swagger/OpenAPI 3 |
| Container | Docker & Docker Compose |

## 🔍 API 테스트

### Swagger UI 사용법

1. **접속**: http://localhost:8080/swagger-ui/index.html
2. **인증 설정**:
   - **관리자 API**: `Authorize` → Basic Auth → `admin` / `1212`
   - **사용자 JWT API**: 
     1. Basic Auth가 설정되어 있다면 먼저 **Logout** 클릭
     2. 로그인 API로 JWT 토큰 획득
     3. `Authorize` → Bearer Token에 토큰 입력

> ⚠️ **중요**: JWT 기반 사용자 API(`/api/users/me`, `/api/users/login` 등)를 테스트할 때는 Basic Auth를 먼저 로그아웃해야 정상 동작합니다.

### 주요 API 엔드포인트

| 기능 | 메소드 | 엔드포인트 | 인증 | 비고                |
|-----|-------|-----------|------|-------------------|
| 회원가입 | POST | `/api/users/register` | 없음 |                   |
| 로그인 | POST | `/api/users/login` | 없음 |                   |
| 내 정보 조회 | GET | `/api/users/me` | JWT | Basic Auth 로그아웃 필요 |
| 전체 회원 조회 | GET | `/api/admin/users` | Basic |                   |
| 회원 상세 조회 | GET | `/api/admin/users/{id}` | Basic |                   |
| 회원 정보 수정 | PUT | `/api/admin/users/{id}` | Basic | 암호, 주소만 수정 가능     |
| 회원 삭제 | DELETE | `/api/admin/users/{id}` | Basic |                   |
| 메시지 발송 | POST | `/api/admin/messages` | Basic |                   |

## 🔧 개발 및 디버깅

### 로그 확인
```bash
# 전체 로그
docker-compose logs -f

# 애플리케이션 로그만
docker-compose logs -f app

# 데이터베이스 로그
docker-compose logs -f postgres
```

### 코드 변경 후 재빌드
```bash
# 애플리케이션 이미지 재빌드
docker-compose build app
docker-compose up -d app

# 또는 전체 재시작
docker-compose down
docker-compose up -d
```

### 로컬 테스트
```bash
# 단위 테스트 실행
./gradlew test

# 전체 빌드
./gradlew build
```

## ❌ 문제 해결

### 포트 충돌
```bash
# 사용 중인 포트 확인
lsof -i :8080
lsof -i :5432

# 다른 서비스 중지 후 재시작
docker-compose down
docker-compose up -d
```

### 데이터베이스 문제
```bash
# DB 상태 확인
docker-compose ps postgres
docker-compose logs postgres

# DB 재시작
docker-compose restart postgres
```

### 완전 초기화
```bash
# 모든 데이터 삭제 후 재시작
docker-compose down -v
docker-compose up -d
```

### 서비스 상태 확인
```bash
# 실행 중인 서비스 확인
docker-compose ps

# 특정 서비스 상태
docker-compose logs [service-name]
```

## 📁 프로젝트 구조

```
join_admin/
├── src/main/java/com/example/joinadmin/
│   ├── config/          # 설정 (Security, Swagger, etc.)
│   ├── controller/      # REST API 컨트롤러
│   ├── dto/            # 요청/응답 객체
│   ├── entity/         # JPA 엔티티
│   ├── repository/     # 데이터베이스 접근
│   ├── service/        # 비즈니스 로직
│   └── util/           # 유틸리티 (JWT, Age 계산)
├── src/test/           # 테스트 코드
├── wiremock/           # Mock 서버 설정
└── docker-compose.yml  # 서비스 구성
```

---

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.