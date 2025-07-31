package com.example.joinadmin.dto;

import com.example.joinadmin.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserDetailResponseTest {
    
    @Test
    @DisplayName("주소 추출 - 서울특별시")
    void addressExtraction_Seoul_ShouldReturnSeoul() {
        // Given
        User user = createTestUser("서울특별시 강남구 테헤란로 123");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("서울특별시");
    }
    
    @Test
    @DisplayName("주소 추출 - 부산광역시")
    void addressExtraction_Busan_ShouldReturnBusan() {
        // Given
        User user = createTestUser("부산광역시 해운대구 센텀남대로 456");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("부산광역시");
    }
    
    @Test
    @DisplayName("주소 추출 - 경기도")
    void addressExtraction_Gyeonggi_ShouldReturnGyeonggi() {
        // Given
        User user = createTestUser("경기도 성남시 분당구 판교역로 789");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("경기도");
    }
    
    @Test
    @DisplayName("주소 추출 - 강원특별자치도")
    void addressExtraction_Gangwon_ShouldReturnGangwon() {
        // Given
        User user = createTestUser("강원특별자치도 춘천시 중앙로 101");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("강원특별자치도");
    }
    
    @Test
    @DisplayName("주소 추출 - 제주특별자치도")
    void addressExtraction_Jeju_ShouldReturnJeju() {
        // Given
        User user = createTestUser("제주특별자치도 제주시 연동 222");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("제주특별자치도");
    }
    
    @Test
    @DisplayName("주소 추출 - 세종특별자치시")
    void addressExtraction_Sejong_ShouldReturnSejong() {
        // Given
        User user = createTestUser("세종특별자치시 한누리대로 2130");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("세종특별자치시");
    }
    
    @Test
    @DisplayName("주소 추출 - 대전광역시")
    void addressExtraction_Daejeon_ShouldReturnDaejeon() {
        // Given
        User user = createTestUser("대전광역시 유성구 대학로 333");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("대전광역시");
    }
    
    @Test
    @DisplayName("주소 추출 - 충청남도")
    void addressExtraction_Chungnam_ShouldReturnChungnam() {
        // Given
        User user = createTestUser("충청남도 천안시 서북구 직산읍");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("충청남도");
    }
    
    @Test
    @DisplayName("주소 추출 - 전라북도")
    void addressExtraction_Jeonbuk_ShouldReturnJeonbuk() {
        // Given
        User user = createTestUser("전라북도 전주시 완산구 효자동");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("전라북도");
    }
    
    @Test
    @DisplayName("주소 추출 - 경상북도")
    void addressExtraction_Gyeongbuk_ShouldReturnGyeongbuk() {
        // Given
        User user = createTestUser("경상북도 포항시 북구 흥해읍");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("경상북도");
    }
    
    @Test
    @DisplayName("주소 추출 - 시 단위 (도가 없는 경우)")
    void addressExtraction_CityOnly_ShouldReturnCity() {
        // Given
        User user = createTestUser("창원시 의창구 원이대로 225");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("창원시");
    }
    
    @Test
    @DisplayName("주소 추출 - 군 단위")
    void addressExtraction_County_ShouldReturnCounty() {
        // Given
        User user = createTestUser("화성군 봉담읍 하가등리");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("화성군");
    }
    
    @Test
    @DisplayName("주소 추출 - null 주소")
    void addressExtraction_NullAddress_ShouldReturnEmpty() {
        // Given
        User user = createTestUser(null);
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("");
    }
    
    @Test
    @DisplayName("주소 추출 - 빈 주소")
    void addressExtraction_EmptyAddress_ShouldReturnEmpty() {
        // Given
        User user = createTestUser("");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("");
    }
    
    @Test
    @DisplayName("주소 추출 - 공백만 있는 주소")
    void addressExtraction_WhitespaceAddress_ShouldReturnEmpty() {
        // Given
        User user = createTestUser("   ");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("");
    }
    
    @Test
    @DisplayName("주소 추출 - 패턴에 맞지 않는 주소")
    void addressExtraction_UnknownPattern_ShouldReturnFirstPart() {
        // Given
        User user = createTestUser("알수없는지역 특이한구 이상한동");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("알수없는지역");
    }
    
    @Test
    @DisplayName("주소 추출 - 단일 단어 주소")
    void addressExtraction_SingleWord_ShouldReturnSingleWord() {
        // Given
        User user = createTestUser("서울");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getAddress()).isEqualTo("서울");
    }
    
    @Test
    @DisplayName("UserDetailResponse 모든 필드 검증")
    void userDetailResponse_AllFields_ShouldBeSetCorrectly() {
        // Given
        User user = createTestUser("서울특별시 강남구 테헤란로 123");
        
        // When
        UserDetailResponse response = new UserDetailResponse(user);
        
        // Then
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getAccount()).isEqualTo(user.getAccount());
        assertThat(response.getName()).isEqualTo(user.getName());
        assertThat(response.getResidentNumber()).isEqualTo(user.getResidentNumber());
        assertThat(response.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        assertThat(response.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
        // 주소는 추출된 형태로 확인
        assertThat(response.getAddress()).isEqualTo("서울특별시");
    }
    
    private User createTestUser(String address) {
        User user = new User();
        user.setId(1L);
        user.setAccount("testuser");
        user.setPassword("encodedPassword");
        user.setName("테스트사용자");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress(address);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}