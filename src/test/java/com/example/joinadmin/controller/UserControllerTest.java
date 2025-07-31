package com.example.joinadmin.controller;

import com.example.joinadmin.dto.UserRegistrationRequest;
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
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        // 각 테스트 전에 데이터베이스 초기화
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("사용자 API 상태 확인")
    void healthCheck_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User API is running"));
    }
    
    @Test
    @DisplayName("회원가입 성공 - 모든 필드 정상 입력")
    void registerUser_WithValidData_ShouldReturnSuccess() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "password123",
                "홍길동",
                "1234567890123",
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.userId").exists());
    }
    
    @Test
    @DisplayName("회원가입 실패 - 계정 중복")
    void registerUser_WithDuplicateAccount_ShouldReturnFailure() throws Exception {
        // 기존 사용자 생성
        User existingUser = new User();
        existingUser.setAccount("testuser1");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setName("기존사용자");
        existingUser.setResidentNumber("9876543210987");
        existingUser.setPhoneNumber("01087654321");
        existingUser.setAddress("부산광역시 해운대구");
        userRepository.save(existingUser);
        
        // 동일한 계정으로 회원가입 시도
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1", // 중복 계정
                "password456",
                "홍길동",
                "1234567890123",
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 존재하는 계정입니다."))
                .andExpect(jsonPath("$.userId").isEmpty());
    }
    
    @Test
    @DisplayName("회원가입 실패 - 주민등록번호 중복")
    void registerUser_WithDuplicateResidentNumber_ShouldReturnFailure() throws Exception {
        // 기존 사용자 생성
        User existingUser = new User();
        existingUser.setAccount("existing");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setName("기존사용자");
        existingUser.setResidentNumber("1234567890123");
        existingUser.setPhoneNumber("01087654321");
        existingUser.setAddress("부산광역시 해운대구");
        userRepository.save(existingUser);
        
        // 동일한 주민등록번호로 회원가입 시도
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "password456",
                "홍길동",
                "1234567890123", // 중복 주민등록번호
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 등록된 주민등록번호입니다."))
                .andExpect(jsonPath("$.userId").isEmpty());
    }
    
    @Test
    @DisplayName("회원가입 실패 - 계정 길이 부족")
    void registerUser_WithShortAccount_ShouldReturnValidationError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "abc", // 3자리 (최소 4자리 필요)
                "password123",
                "홍길동",
                "1234567890123",
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("계정은 4자 이상 50자 이하여야 합니다")));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 암호 길이 부족")
    void registerUser_WithShortPassword_ShouldReturnValidationError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "1234567", // 7자리 (최소 8자리 필요)
                "홍길동",
                "1234567890123",
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("암호는 8자 이상 100자 이하여야 합니다")));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 주민등록번호 형식 오류")
    void registerUser_WithInvalidResidentNumber_ShouldReturnValidationError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "password123",
                "홍길동",
                "12345678901", // 11자리 (13자리 필요)
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("주민등록번호는 13자리 숫자여야 합니다")));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 핸드폰번호 형식 오류")
    void registerUser_WithInvalidPhoneNumber_ShouldReturnValidationError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "password123",
                "홍길동",
                "1234567890123",
                "0101234567", // 10자리 (11자리 필요)
                "서울특별시 강남구 테헤란로 123"
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("핸드폰번호는 11자리 숫자여야 합니다")));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 필수 필드 누락")
    void registerUser_WithMissingFields_ShouldReturnValidationError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "", // 계정 누락
                "", // 암호 누락
                "", // 성명 누락
                "", // 주민등록번호 누락
                "", // 핸드폰번호 누락
                ""  // 주소 누락
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("계정은 필수입니다"),
                        org.hamcrest.Matchers.containsString("암호는 필수입니다"),
                        org.hamcrest.Matchers.containsString("성명은 필수입니다"),
                        org.hamcrest.Matchers.containsString("주민등록번호는 필수입니다"),
                        org.hamcrest.Matchers.containsString("핸드폰번호는 필수입니다"),
                        org.hamcrest.Matchers.containsString("주소는 필수입니다")
                )));
    }
    
    @Test
    @DisplayName("회원가입 실패 - JSON 형식 오류")
    void registerUser_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("회원가입 성공 - 경계값 테스트 (최소 길이)")
    void registerUser_WithMinimumValidData_ShouldReturnSuccess() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "abcd", // 최소 4자리
                "12345678", // 최소 8자리
                "김", // 최소 1자리
                "1234567890123", // 정확히 13자리
                "01012345678", // 정확히 11자리
                "서울" // 최소 주소
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.userId").exists());
    }
    
    @Test
    @DisplayName("회원가입 성공 - 경계값 테스트 (최대 길이)")
    void registerUser_WithMaximumValidData_ShouldReturnSuccess() throws Exception {
        // 50자리 계정명
        String maxAccount = "a".repeat(50);
        // 100자리 암호
        String maxPassword = "a".repeat(100);
        // 50자리 성명
        String maxName = "김".repeat(50);
        // 500자리 주소
        String maxAddress = "서울특별시 ".repeat(50);
        
        UserRegistrationRequest request = new UserRegistrationRequest(
                maxAccount,
                maxPassword,
                maxName,
                "1234567890123",
                "01012345678",
                maxAddress
        );
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.userId").exists());
    }
}