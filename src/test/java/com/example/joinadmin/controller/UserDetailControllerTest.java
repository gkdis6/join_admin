package com.example.joinadmin.controller;

import com.example.joinadmin.dto.LoginRequest;
import com.example.joinadmin.dto.LoginResponse;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserDetailControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;
    private String jwtToken;
    
    @BeforeEach
    void setUp() throws Exception {
        // 각 테스트 전에 데이터베이스 초기화
        userRepository.deleteAll();
        
        // 테스트용 사용자 생성
        testUser = new User();
        testUser.setAccount("detailtest");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setName("상세조회테스트");
        testUser.setResidentNumber("1234567890123");
        testUser.setPhoneNumber("01012345678");
        testUser.setAddress("서울특별시 강남구 테헤란로 123");
        testUser = userRepository.save(testUser);
        
        // 로그인하여 JWT 토큰 획득
        LoginRequest loginRequest = new LoginRequest("detailtest", "password123");
        
        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        String loginResponseJson = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(loginResponseJson, LoginResponse.class);
        jwtToken = loginResponse.getToken();
    }
    
    @Test
    @DisplayName("사용자 상세정보 조회 성공 - 유효한 JWT 토큰")
    void getUserDetail_WithValidToken_ShouldReturnUserDetail() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.account").value("detailtest"))
                .andExpect(jsonPath("$.name").value("상세조회테스트"))
                .andExpect(jsonPath("$.residentNumber").value("1234567890123"))
                .andExpect(jsonPath("$.phoneNumber").value("01012345678"))
                .andExpect(jsonPath("$.address").value("서울특별시")) // 가장 큰 단위의 행정구역만
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }
    
    @Test
    @DisplayName("사용자 상세정보 조회 실패 - JWT 토큰 없음")
    void getUserDetail_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }
    
    @Test
    @DisplayName("사용자 상세정보 조회 실패 - 무효한 JWT 토큰")
    void getUserDetail_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }
    
    @Test
    @DisplayName("사용자 상세정보 조회 실패 - 만료된 JWT 토큰")
    void getUserDetail_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
        // 만료된 토큰 (임의로 생성한 잘못된 토큰)
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.invalid";
        
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }
    
    @Test
    @DisplayName("주소 추출 테스트 - 다양한 행정구역")
    void getUserDetail_AddressExtraction_ShouldReturnMainRegion() throws Exception {
        // 다양한 주소 패턴 테스트
        testAddressExtraction("서울특별시 강남구 테헤란로 123", "서울특별시");
        testAddressExtraction("부산광역시 해운대구 센텀남대로 456", "부산광역시");
        testAddressExtraction("경기도 성남시 분당구 판교역로 789", "경기도");
        testAddressExtraction("강원특별자치도 춘천시 중앙로 101", "강원특별자치도");
        testAddressExtraction("제주특별자치도 제주시 연동 222", "제주특별자치도");
        testAddressExtraction("대전광역시 유성구 대학로 333", "대전광역시");
        testAddressExtraction("충청남도 천안시 서북구 직산읍", "충청남도");
    }
    
    private void testAddressExtraction(String fullAddress, String expectedRegion) throws Exception {
        // 새 사용자 생성
        User addressTestUser = new User();
        addressTestUser.setAccount("addresstest" + System.currentTimeMillis());
        addressTestUser.setPassword(passwordEncoder.encode("password123"));
        addressTestUser.setName("주소테스트");
        addressTestUser.setResidentNumber(String.valueOf(System.currentTimeMillis()).substring(0, 13));
        addressTestUser.setPhoneNumber("01099999999");
        addressTestUser.setAddress(fullAddress);
        addressTestUser = userRepository.save(addressTestUser);
        
        // 로그인
        LoginRequest loginRequest = new LoginRequest(addressTestUser.getAccount(), "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        String loginResponseJson = loginResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(loginResponseJson, LoginResponse.class);
        
        // 상세정보 조회 및 주소 확인
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(expectedRegion));
        
        // 테스트 사용자 정리
        userRepository.delete(addressTestUser);
    }
    
    @Test
    @DisplayName("Bearer 토큰 형식 검증")
    void getUserDetail_TokenFormat_ShouldWork() throws Exception {
        // 정상적인 Bearer 형식
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
        
        // Bearer 없는 형식 (실패해야 함)
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", jwtToken))
                .andExpect(status().isUnauthorized());
        
        // 다른 인증 방식 (실패해야 함)
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Basic " + jwtToken))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자 토큰으로 조회")
    void getUserDetail_WithDeletedUser_ShouldReturnNotFound() throws Exception {
        // 사용자 삭제
        userRepository.delete(testUser);
        
        // 삭제된 사용자의 토큰으로 조회 시도
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("응답 형식 검증 - 모든 필드 존재")
    void getUserDetail_ResponseFormat_ShouldContainAllFields() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.account").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.residentNumber").exists())
                .andExpect(jsonPath("$.phoneNumber").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }
}