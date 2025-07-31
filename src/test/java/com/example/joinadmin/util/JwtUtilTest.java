package com.example.joinadmin.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    
    @BeforeEach
    void setUp() {
        // 테스트용 JwtUtil 인스턴스 생성
        String secret = "myTestSecretKeyThatIsLongEnoughForHS256Algorithm";
        long expiration = 86400000; // 24시간
        jwtUtil = new JwtUtil(secret, expiration);
    }
    
    @Test
    @DisplayName("JWT 토큰 생성 - 정상적인 계정과 사용자 ID")
    void generateToken_WithValidAccountAndUserId_ShouldReturnToken() {
        // Given
        String account = "testuser1";
        Long userId = 123L;
        
        // When
        String token = jwtUtil.generateToken(account, userId);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 header.payload.signature 구조
    }
    
    @Test
    @DisplayName("JWT 토큰에서 계정 추출")
    void getAccountFromToken_WithValidToken_ShouldReturnAccount() {
        // Given
        String account = "testuser1";
        Long userId = 123L;
        String token = jwtUtil.generateToken(account, userId);
        
        // When
        String extractedAccount = jwtUtil.getAccountFromToken(token);
        
        // Then
        assertThat(extractedAccount).isEqualTo(account);
    }
    
    @Test
    @DisplayName("JWT 토큰에서 사용자 ID 추출")
    void getUserIdFromToken_WithValidToken_ShouldReturnUserId() {
        // Given
        String account = "testuser1";
        Long userId = 123L;
        String token = jwtUtil.generateToken(account, userId);
        
        // When
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);
        
        // Then
        assertThat(extractedUserId).isEqualTo(userId);
    }
    
    @Test
    @DisplayName("JWT 토큰 유효성 검증 - 유효한 토큰")
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String account = "testuser1";
        Long userId = 123L;
        String token = jwtUtil.generateToken(account, userId);
        
        // When
        boolean isValid = jwtUtil.validateToken(token);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("JWT 토큰 유효성 검증 - 무효한 토큰")
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("JWT 토큰 유효성 검증 - null 토큰")
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtUtil.validateToken(null);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("JWT 토큰 유효성 검증 - 빈 토큰")
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtUtil.validateToken("");
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("JWT 토큰 만료 검증 - 유효한 토큰")
    void isTokenExpired_WithValidToken_ShouldReturnFalse() {
        // Given
        String account = "testuser1";
        Long userId = 123L;
        String token = jwtUtil.generateToken(account, userId);
        
        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);
        
        // Then
        assertThat(isExpired).isFalse();
    }
    
    @Test
    @DisplayName("JWT 토큰 만료 검증 - 무효한 토큰")
    void isTokenExpired_WithInvalidToken_ShouldReturnTrue() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When
        boolean isExpired = jwtUtil.isTokenExpired(invalidToken);
        
        // Then
        assertThat(isExpired).isTrue();
    }
    
    @Test
    @DisplayName("JWT 토큰 만료 검증 - 만료된 토큰")
    void isTokenExpired_WithExpiredToken_ShouldReturnTrue() {
        // Given - 만료시간을 1ms로 설정하여 즉시 만료되는 토큰 생성
        JwtUtil shortExpirationJwtUtil = new JwtUtil("myTestSecretKeyThatIsLongEnoughForHS256Algorithm", 1);
        String token = shortExpirationJwtUtil.generateToken("testuser1", 123L);
        
        // 토큰이 만료될 때까지 잠시 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // When
        boolean isExpired = shortExpirationJwtUtil.isTokenExpired(token);
        
        // Then
        assertThat(isExpired).isTrue();
    }
    
    @Test
    @DisplayName("다양한 계정명과 사용자 ID로 토큰 생성 및 검증")
    void generateAndValidateToken_WithVariousInputs_ShouldWork() {
        // Test data
        String[] accounts = {"user1", "testAccount", "admin@example.com", "user_with_underscore"};
        Long[] userIds = {1L, 999L, 123456L, Long.MAX_VALUE};
        
        for (int i = 0; i < accounts.length; i++) {
            String account = accounts[i];
            Long userId = userIds[i];
            
            // Given & When
            String token = jwtUtil.generateToken(account, userId);
            
            // Then
            assertThat(jwtUtil.validateToken(token)).isTrue();
            assertThat(jwtUtil.getAccountFromToken(token)).isEqualTo(account);
            assertThat(jwtUtil.getUserIdFromToken(token)).isEqualTo(userId);
            assertThat(jwtUtil.isTokenExpired(token)).isFalse();
        }
    }
}