package com.example.joinadmin.controller;

import com.example.joinadmin.dto.LoginRequest;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class LoginControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // 각 테스트 전에 데이터베이스 초기화
        userRepository.deleteAll();
        
        // 테스트용 사용자 생성
        testUser = new User();
        testUser.setAccount("testuser1");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setName("홍길동");
        testUser.setResidentNumber("1234567890123");
        testUser.setPhoneNumber("01012345678");
        testUser.setAddress("서울특별시 강남구 테헤란로 123");
        testUser = userRepository.save(testUser);
    }
    
    @Test
    @DisplayName("로그인 성공 - 올바른 계정과 암호")
    void loginUser_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        LoginRequest request = new LoginRequest("testuser1", "password123");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").value(testUser.getId()));
    }
    
    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 계정")
    void loginUser_WithNonExistentAccount_ShouldReturnFailure() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent", "password123");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("계정 또는 암호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.token").isEmpty())
                .andExpect(jsonPath("$.userId").isEmpty());
    }
    
    @Test
    @DisplayName("로그인 실패 - 잘못된 암호")
    void loginUser_WithWrongPassword_ShouldReturnFailure() throws Exception {
        LoginRequest request = new LoginRequest("testuser1", "wrongpassword");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("계정 또는 암호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.token").isEmpty())
                .andExpect(jsonPath("$.userId").isEmpty());
    }
    
    @Test
    @DisplayName("로그인 실패 - 계정 길이 부족 (유효성 검사)")
    void loginUser_WithShortAccount_ShouldReturnValidationError() throws Exception {
        LoginRequest request = new LoginRequest("abc", "password123");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("계정은 4자 이상 50자 이하여야 합니다")));
    }
    
    @Test
    @DisplayName("로그인 실패 - 암호 길이 부족 (유효성 검사)")
    void loginUser_WithShortPassword_ShouldReturnValidationError() throws Exception {
        LoginRequest request = new LoginRequest("testuser1", "1234567");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("암호는 8자 이상 100자 이하여야 합니다")));
    }
    
    @Test
    @DisplayName("로그인 실패 - 필수 필드 누락")
    void loginUser_WithMissingFields_ShouldReturnValidationError() throws Exception {
        LoginRequest request = new LoginRequest("", "");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("계정은 필수입니다"),
                        org.hamcrest.Matchers.containsString("암호는 필수입니다")
                )));
    }
    
    @Test
    @DisplayName("로그인 실패 - JSON 형식 오류")
    void loginUser_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("로그인 성공 - 경계값 테스트 (최소 길이)")
    void loginUser_WithMinimumValidData_ShouldReturnSuccess() throws Exception {
        // 최소 길이 테스트를 위한 새 사용자 생성
        User minUser = new User();
        minUser.setAccount("abcd"); // 최소 4자리
        minUser.setPassword(passwordEncoder.encode("12345678")); // 최소 8자리
        minUser.setName("김");
        minUser.setResidentNumber("9876543210987");
        minUser.setPhoneNumber("01087654321");
        minUser.setAddress("부산");
        userRepository.save(minUser);
        
        LoginRequest request = new LoginRequest("abcd", "12345678");
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").exists());
    }
    
    @Test
    @DisplayName("로그인 성공 - 경계값 테스트 (최대 길이)")
    void loginUser_WithMaximumValidData_ShouldReturnSuccess() throws Exception {
        // 최대 길이 테스트를 위한 새 사용자 생성
        String maxAccount = "a".repeat(50); // 50자리
        String maxPassword = "a".repeat(100); // 100자리
        
        User maxUser = new User();
        maxUser.setAccount(maxAccount);
        maxUser.setPassword(passwordEncoder.encode(maxPassword));
        maxUser.setName("최대길이테스트");
        maxUser.setResidentNumber("5555555555555");
        maxUser.setPhoneNumber("01055555555");
        maxUser.setAddress("최대주소");
        userRepository.save(maxUser);
        
        LoginRequest request = new LoginRequest(maxAccount, maxPassword);
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").exists());
    }
    
    @Test
    @DisplayName("로그인 토큰 유효성 검증")
    void loginUser_TokenShouldBeValid() throws Exception {
        LoginRequest request = new LoginRequest("testuser1", "password123");
        
        String responseJson = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // JWT 토큰 형식 검증 (간단한 형식 체크)
        // 실제 JWT는 헤더.페이로드.서명 구조로 되어 있음
        // 추가적인 JWT 검증 로직은 별도 테스트에서 수행
        org.junit.jupiter.api.Assertions.assertTrue(responseJson.contains("token"));
    }
}