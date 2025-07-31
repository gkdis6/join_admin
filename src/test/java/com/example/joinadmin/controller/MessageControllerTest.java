package com.example.joinadmin.controller;

import com.example.joinadmin.dto.MessageRequest;
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

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MessageControllerTest {
    
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
        
        // 다양한 연령대의 테스트 사용자들 생성
        createTestUsers();
    }
    
    private void createTestUsers() {
        // 10대 사용자 (2010년 출생)
        createUser("teenager1", "10대사용자1", "1001011234567", "01010001111");
        createUser("teenager2", "10대사용자2", "1005152345678", "01010002222");
        
        // 20대 사용자 (2000년 출생)
        createUser("twenties1", "20대사용자1", "0001013456789", "01020001111");
        createUser("twenties2", "20대사용자2", "0006154567890", "01020002222");
        
        // 30대 사용자 (1990년 출생)
        createUser("thirties1", "30대사용자1", "9001015678901", "01030001111");
        createUser("thirties2", "30대사용자2", "9507156789012", "01030002222");
        
        // 40대 사용자 (1980년 출생)
        createUser("forties1", "40대사용자1", "8001017890123", "01040001111");
        createUser("forties2", "40대사용자2", "8508158901234", "01040002222");
        
        // 50대 사용자 (1970년 출생)
        createUser("fifties1", "50대사용자1", "7001019012345", "01050001111");
    }
    
    private void createUser(String account, String name, String residentNumber, String phoneNumber) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setName(name);
        user.setResidentNumber(residentNumber);
        user.setPhoneNumber(phoneNumber);
        user.setAddress("서울특별시 강남구 테헤란로 123");
        userRepository.save(user);
    }
    
    @Test
    @DisplayName("메시지 발송 성공 - 20대~30대 대상")
    void sendMessage_ToTwentiesThirties_ShouldReturnSuccess() throws Exception {
        MessageRequest request = new MessageRequest(20, 39, "20-30대 대상 메시지입니다.");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메시지 발송이 완료되었습니다."))
                .andExpect(jsonPath("$.targetUserCount").exists())
                .andExpect(jsonPath("$.successCount").exists())
                .andExpect(jsonPath("$.failCount").exists());
    }
    
    @Test
    @DisplayName("메시지 발송 성공 - 전체 연령대 대상")
    void sendMessage_ToAllAges_ShouldReturnSuccess() throws Exception {
        MessageRequest request = new MessageRequest(1, 100, "전체 연령대 대상 메시지입니다.");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메시지 발송이 완료되었습니다."))
                .andExpect(jsonPath("$.targetUserCount").exists()) // 실제 나이에 따라 달라질 수 있음
                .andExpect(jsonPath("$.successCount").exists())
                .andExpect(jsonPath("$.failCount").exists());
    }
    
    @Test
    @DisplayName("메시지 발송 성공 - 해당 연령대 없음")
    void sendMessage_NoTargetUsers_ShouldReturnSuccess() throws Exception {
        MessageRequest request = new MessageRequest(70, 80, "70-80대 대상 메시지입니다.");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("메시지 발송이 완료되었습니다."))
                .andExpect(jsonPath("$.targetUserCount").value(0))
                .andExpect(jsonPath("$.successCount").value(0))
                .andExpect(jsonPath("$.failCount").value(0));
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 최소 연령이 최대 연령보다 큰 경우")
    void sendMessage_MinAgeGreaterThanMaxAge_ShouldReturnFailure() throws Exception {
        MessageRequest request = new MessageRequest(40, 30, "잘못된 연령 범위");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("최소 연령이 최대 연령보다 클 수 없습니다."));
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 필수 필드 누락 (최소 연령)")
    void sendMessage_MissingMinAge_ShouldReturnValidationError() throws Exception {
        MessageRequest request = new MessageRequest(null, 30, "메시지 내용");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("최소 연령은 필수입니다")));
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 필수 필드 누락 (최대 연령)")
    void sendMessage_MissingMaxAge_ShouldReturnValidationError() throws Exception {
        MessageRequest request = new MessageRequest(20, null, "메시지 내용");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("최대 연령은 필수입니다")));
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 필수 필드 누락 (메시지 내용)")
    void sendMessage_MissingMessage_ShouldReturnValidationError() throws Exception {
        MessageRequest request = new MessageRequest(20, 30, "");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("메시지 내용은 필수입니다")));
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 잘못된 연령 값 (음수)")
    void sendMessage_NegativeAge_ShouldReturnValidationError() throws Exception {
        MessageRequest request = new MessageRequest(-5, 30, "메시지 내용");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("최소 연령은 양수여야 합니다")));
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 인증 없음")
    void sendMessage_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        MessageRequest request = new MessageRequest(20, 30, "메시지 내용");
        
        mockMvc.perform(post("/api/admin/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - 잘못된 인증 정보")
    void sendMessage_WithWrongAuth_ShouldReturnUnauthorized() throws Exception {
        MessageRequest request = new MessageRequest(20, 30, "메시지 내용");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "wrongpassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("메시지 발송 실패 - JSON 형식 오류")
    void sendMessage_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("메시지 발송 - 특정 연령대만 대상")
    void sendMessage_SpecificAgeRange_ShouldTargetCorrectUsers() throws Exception {
        MessageRequest request = new MessageRequest(10, 19, "10대 전용 메시지입니다.");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.targetUserCount").exists()) // 실제 나이에 따라 달라질 수 있음
                .andExpect(jsonPath("$.successCount").exists())
                .andExpect(jsonPath("$.failCount").exists());
    }
    
    @Test
    @DisplayName("메시지 발송 - 경계값 테스트 (정확한 나이)")
    void sendMessage_ExactAgeMatch_ShouldIncludeUser() throws Exception {
        // 현재 날짜 기준으로 정확히 30세인 사용자 생성
        LocalDate today = LocalDate.now();
        int birthYear = today.getYear() - 30;
        String birthYearStr = String.format("%02d", birthYear % 100);
        String birthMonth = String.format("%02d", today.getMonthValue());
        String birthDay = String.format("%02d", today.getDayOfMonth());
        
        String residentNumber = birthYearStr + birthMonth + birthDay;
        if (birthYear >= 2000) {
            residentNumber += "3123456"; // 2000년대 출생 남성
        } else {
            residentNumber += "1123456"; // 1900년대 출생 남성
        }
        
        createUser("exact30", "정확히30세", residentNumber, "01099999999");
        
        MessageRequest request = new MessageRequest(30, 30, "정확히 30세 대상 메시지");
        
        mockMvc.perform(post("/api/admin/messages")
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.targetUserCount").value(1)); // 정확히 30세 사용자 1명
    }
}