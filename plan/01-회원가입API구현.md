# 1번 요구사항: 회원가입 API 구현 계획

## 요구사항 분석
- **입력 필드**: 계정, 암호, 성명, 주민등록번호, 핸드폰번호, 주소
- **유일성 제약**: 계정과 주민등록번호는 중복 불가
- **검증**: 핸드폰번호 11자리, 주민등록번호 형식 (실제 검증은 필요 없음)
- **응답**: 단순 성공/실패 처리

## 구현 계획

### 1. Entity 생성 (User.java) ✅
- JPA 엔티티로 회원 정보 테이블 정의
- 계정, 주민등록번호에 유니크 제약 조건 추가
- 기본적인 validation annotation 적용

### 2. DTO 생성 ✅
- **UserRegistrationRequest**: 회원가입 요청 DTO
- **UserRegistrationResponse**: 회원가입 응답 DTO
- Bean Validation을 통한 입력값 검증

### 3. Repository 생성 (UserRepository.java)
- JpaRepository 상속
- 계정, 주민등록번호 중복 체크용 메서드 추가

### 4. Service 생성 (UserService.java)
- 회원가입 비즈니스 로직 구현
- 중복 검증 로직
- 암호 해싱 (Spring Security의 BCryptPasswordEncoder 사용)

### 5. Controller 생성 (UserController.java)
- POST /api/users/register 엔드포인트
- 요청 검증 및 응답 처리
- 예외 처리

### 6. 테스트
- 프로젝트 빌드 및 기본 동작 확인
- API 테스트 (성공/실패 케이스)

## 진행 상황
- [x] User Entity 생성
- [x] UserRegistrationRequest DTO 생성  
- [x] UserRegistrationResponse DTO 생성
- [x] UserRepository 생성
- [x] UserService 생성
- [x] UserController 생성
- [x] Security Config 생성
- [x] Docker Compose 설정
- [x] API 테스트 완료

## 테스트 결과
✅ 정상 회원가입: userId=1로 성공
✅ 계정 중복 검증: "이미 존재하는 계정입니다" 메시지
✅ 주민등록번호 중복 검증: "이미 등록된 주민등록번호입니다" 메시지  
✅ 입력값 검증: 각 필드별 상세한 오류 메시지 제공

## 완료 상태
**1번 요구사항 회원가입 API 구현 완료! ✅**

## 작성일
2025-07-31