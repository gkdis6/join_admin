# 2번 요구사항: 시스템 관리자 API 구현 계획

## 요구사항 분석
- **관리자 인증**: Basic Auth (admin/1212)
- **회원 조회**: Pagination 기반
- **회원 수정**: 암호, 주소만 수정 가능 (개별 또는 동시 수정)
- **회원 삭제**: 회원 삭제 기능

## 구현 계획

### 1. 관리자 인증 설정 (Basic Auth)
- SecurityConfig에서 관리자 API 경로에 대한 Basic Auth 설정
- admin/1212 인증 정보 설정

### 2. 회원 조회 API (Pagination)
- **엔드포인트**: GET /api/admin/users
- **파라미터**: page, size, sort
- **응답**: 페이징된 회원 목록

### 3. 회원 수정 API
- **엔드포인트**: PUT /api/admin/users/{id}
- **수정 가능 필드**: password, address
- **유연한 수정**: 둘 중 하나만 또는 둘 다 수정 가능

### 4. 회원 삭제 API
- **엔드포인트**: DELETE /api/admin/users/{id}
- **응답**: 삭제 성공/실패 메시지

### 5. DTO 생성
- UserUpdateRequest: 수정 요청 DTO
- UserResponse: 조회 응답 DTO
- PagedResponse: 페이징 응답 DTO

## 구현 순서
1. ✅ 관리자 인증 설정
2. ✅ DTO 클래스 생성
3. ✅ AdminController 생성
4. ✅ Service 메서드 추가
5. ✅ API 테스트 (수동)
6. 🔄 테스트 코드 작성

## 테스트 결과
✅ Basic Auth 인증: admin/1212로 성공적으로 인증
✅ 회원 목록 조회: 페이징 기능 정상 작동
✅ 특정 회원 조회: ID로 회원 정보 조회 성공
✅ 회원 수정 (암호만): 암호 변경 성공
✅ 회원 수정 (주소만): 주소 변경 성공
✅ 회원 수정 (암호+주소): 동시 수정 성공
✅ 회원 삭제: 삭제 후 목록에서 제거 확인
✅ 인증 실패: 인증 없이 접근 시 401 오류

## 완료 상태
**2번 요구사항 시스템 관리자 API 구현 완료! ✅**

## 작성일
2025-07-31