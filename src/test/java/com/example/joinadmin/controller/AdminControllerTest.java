package com.example.joinadmin.controller;

import com.example.joinadmin.dto.UserUpdateRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdminControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser1;
    private User testUser2;
    
    @BeforeEach
    void setUp() {
        // 테스트용 사용자 데이터 생성
        testUser1 = new User();
        testUser1.setAccount("testuser1");
        testUser1.setPassword(passwordEncoder.encode("password123"));
        testUser1.setName("홍길동");
        testUser1.setResidentNumber("1234567890123");
        testUser1.setPhoneNumber("01012345678");
        testUser1.setAddress("서울특별시 강남구 테헤란로 123");
        testUser1 = userRepository.save(testUser1);
        
        testUser2 = new User();
        testUser2.setAccount("testuser2");
        testUser2.setPassword(passwordEncoder.encode("password456"));
        testUser2.setName("김철수");
        testUser2.setResidentNumber("9876543210987");
        testUser2.setPhoneNumber("01087654321");
        testUser2.setAddress("부산광역시 해운대구 센텀로 456");
        testUser2 = userRepository.save(testUser2);
    }
    
    @Test
    @DisplayName("관리자 API 상태 확인 - 인증 성공")
    void healthCheck_WithValidAuth_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/admin/health")
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin API is running"));
    }
    
    @Test
    @DisplayName("관리자 API 상태 확인 - 인증 실패")
    void healthCheck_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/health"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("회원 목록 조회 - 기본 페이징")
    void getUsers_WithDefaultPaging_ShouldReturnPagedUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }
    
    @Test
    @DisplayName("회원 목록 조회 - 커스텀 페이징")
    void getUsers_WithCustomPaging_ShouldReturnPagedUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .param("page", "0")
                .param("size", "1")
                .param("sort", "id")
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }
    
    @Test
    @DisplayName("특정 회원 조회 - 존재하는 회원")
    void getUser_WithValidId_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/admin/users/{id}", testUser1.getId())
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser1.getId()))
                .andExpect(jsonPath("$.account").value("testuser1"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.residentNumber").value("1234567890123"))
                .andExpect(jsonPath("$.phoneNumber").value("01012345678"))
                .andExpect(jsonPath("$.address").value("서울특별시 강남구 테헤란로 123"));
    }
    
    @Test
    @DisplayName("특정 회원 조회 - 존재하지 않는 회원")
    void getUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/users/{id}", 999L)
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("회원 수정 - 암호만 변경")
    void updateUser_WithPasswordOnly_ShouldUpdateSuccessfully() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newpassword123");
        
        mockMvc.perform(put("/api/admin/users/{id}", testUser1.getId())
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원 정보가 성공적으로 수정되었습니다."));
    }
    
    @Test
    @DisplayName("회원 수정 - 주소만 변경")
    void updateUser_WithAddressOnly_ShouldUpdateSuccessfully() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setAddress("대구광역시 중구 동성로 999");
        
        mockMvc.perform(put("/api/admin/users/{id}", testUser1.getId())
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원 정보가 성공적으로 수정되었습니다."));
    }
    
    @Test
    @DisplayName("회원 수정 - 암호와 주소 동시 변경")
    void updateUser_WithPasswordAndAddress_ShouldUpdateSuccessfully() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newpassword456");
        request.setAddress("인천광역시 연수구 센트럴로 777");
        
        mockMvc.perform(put("/api/admin/users/{id}", testUser1.getId())
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원 정보가 성공적으로 수정되었습니다."));
    }
    
    @Test
    @DisplayName("회원 수정 - 수정할 내용 없음")
    void updateUser_WithNoUpdates_ShouldReturnBadRequest() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        
        mockMvc.perform(put("/api/admin/users/{id}", testUser1.getId())
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("수정할 정보가 없습니다. 암호 또는 주소를 입력해주세요."));
    }
    
    @Test
    @DisplayName("회원 수정 - 존재하지 않는 회원")
    void updateUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newpassword123");
        
        mockMvc.perform(put("/api/admin/users/{id}", 999L)
                .with(httpBasic("admin", "1212"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("회원을 찾을 수 없거나 수정에 실패했습니다."));
    }
    
    @Test
    @DisplayName("회원 삭제 - 존재하는 회원")
    void deleteUser_WithValidId_ShouldDeleteSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/admin/users/{id}", testUser1.getId())
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원이 성공적으로 삭제되었습니다."));
    }
    
    @Test
    @DisplayName("회원 삭제 - 존재하지 않는 회원")
    void deleteUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/admin/users/{id}", 999L)
                .with(httpBasic("admin", "1212")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("회원을 찾을 수 없거나 삭제에 실패했습니다."));
    }
    
    @Test
    @DisplayName("관리자 API 인증 실패 테스트")
    void adminApi_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isUnauthorized());
    }
}