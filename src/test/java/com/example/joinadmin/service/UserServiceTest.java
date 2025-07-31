package com.example.joinadmin.service;

import com.example.joinadmin.dto.UserRegistrationRequest;
import com.example.joinadmin.dto.UserRegistrationResponse;
import com.example.joinadmin.dto.UserUpdateRequest;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("회원가입 성공 - 정상 데이터")
    void registerUser_WithValidData_ShouldReturnSuccess() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "password123",
                "홍길동",
                "1234567890123",
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        // When
        UserRegistrationResponse response = userService.registerUser(request);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("회원가입이 성공적으로 완료되었습니다.");
        assertThat(response.getUserId()).isNotNull();
        
        // 데이터베이스에 저장되었는지 확인
        User savedUser = userRepository.findById(response.getUserId()).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getAccount()).isEqualTo("testuser1");
        assertThat(savedUser.getName()).isEqualTo("홍길동");
        assertThat(savedUser.getResidentNumber()).isEqualTo("1234567890123");
        assertThat(savedUser.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(savedUser.getAddress()).isEqualTo("서울특별시 강남구 테헤란로 123");
        
        // 비밀번호가 암호화되었는지 확인
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }
    
    @Test
    @DisplayName("회원가입 실패 - 계정 중복")
    void registerUser_WithDuplicateAccount_ShouldReturnFailure() {
        // Given - 기존 사용자 등록
        User existingUser = new User();
        existingUser.setAccount("testuser1");
        existingUser.setPassword(passwordEncoder.encode("existing"));
        existingUser.setName("기존사용자");
        existingUser.setResidentNumber("9876543210987");
        existingUser.setPhoneNumber("01087654321");
        existingUser.setAddress("부산광역시");
        userRepository.save(existingUser);
        
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1", // 중복 계정
                "password123",
                "홍길동",
                "1234567890123",
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        // When
        UserRegistrationResponse response = userService.registerUser(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("이미 존재하는 계정입니다.");
        assertThat(response.getUserId()).isNull();
    }
    
    @Test
    @DisplayName("회원가입 실패 - 주민등록번호 중복")
    void registerUser_WithDuplicateResidentNumber_ShouldReturnFailure() {
        // Given - 기존 사용자 등록
        User existingUser = new User();
        existingUser.setAccount("existing");
        existingUser.setPassword(passwordEncoder.encode("existing"));
        existingUser.setName("기존사용자");
        existingUser.setResidentNumber("1234567890123");
        existingUser.setPhoneNumber("01087654321");
        existingUser.setAddress("부산광역시");
        userRepository.save(existingUser);
        
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser1",
                "password123",
                "홍길동",
                "1234567890123", // 중복 주민등록번호
                "01012345678",
                "서울특별시 강남구 테헤란로 123"
        );
        
        // When
        UserRegistrationResponse response = userService.registerUser(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("이미 등록된 주민등록번호입니다.");
        assertThat(response.getUserId()).isNull();
    }
    
    @Test
    @DisplayName("계정으로 사용자 조회 - 존재하는 사용자")
    void findByAccount_WithExistingAccount_ShouldReturnUser() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        // When
        User foundUser = userService.findByAccount("testuser1");
        
        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getAccount()).isEqualTo("testuser1");
        assertThat(foundUser.getName()).isEqualTo("홍길동");
    }
    
    @Test
    @DisplayName("계정으로 사용자 조회 - 존재하지 않는 사용자")
    void findByAccount_WithNonExistingAccount_ShouldReturnNull() {
        // When
        User foundUser = userService.findByAccount("nonexistent");
        
        // Then
        assertThat(foundUser).isNull();
    }
    
    @Test
    @DisplayName("ID로 사용자 조회 - 존재하는 사용자")
    void findById_WithExistingId_ShouldReturnUser() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        // When
        User foundUser = userService.findById(savedUser.getId());
        
        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getAccount()).isEqualTo("testuser1");
    }
    
    @Test
    @DisplayName("ID로 사용자 조회 - 존재하지 않는 사용자")
    void findById_WithNonExistingId_ShouldReturnNull() {
        // When
        User foundUser = userService.findById(999L);
        
        // Then
        assertThat(foundUser).isNull();
    }
    
    @Test
    @DisplayName("모든 사용자 조회 - 페이징")
    void findAllUsers_WithPaging_ShouldReturnPagedUsers() {
        // Given - 테스트 데이터 생성
        for (int i = 1; i <= 15; i++) {
            User user = new User();
            user.setAccount("testuser" + i);
            user.setPassword(passwordEncoder.encode("password"));
            user.setName("사용자" + i);
            user.setResidentNumber(String.format("12345678901%02d", i)); // 13자리 유니크하게 생성
            user.setPhoneNumber(String.format("010123456%02d", i)); // 11자리 유니크하게 생성
            user.setAddress("서울시 " + i);
            userRepository.save(user);
        }
        
        // When
        Pageable pageable = PageRequest.of(0, 10); // 첫 번째 페이지, 10개씩
        Page<User> userPage = userService.findAllUsers(pageable);
        
        // Then
        assertThat(userPage.getContent()).hasSize(10);
        assertThat(userPage.getTotalElements()).isEqualTo(15);
        assertThat(userPage.getTotalPages()).isEqualTo(2);
        assertThat(userPage.isFirst()).isTrue();
        assertThat(userPage.isLast()).isFalse();
    }
    
    @Test
    @DisplayName("사용자 수정 성공 - 암호만 변경")
    void updateUser_WithPasswordOnly_ShouldReturnTrue() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("oldpassword"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newpassword123");
        
        // When
        boolean result = userService.updateUser(savedUser.getId(), request);
        
        // Then
        assertThat(result).isTrue();
        
        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(passwordEncoder.matches("newpassword123", updatedUser.getPassword())).isTrue();
        assertThat(updatedUser.getAddress()).isEqualTo("서울시"); // 주소는 변경되지 않음
    }
    
    @Test
    @DisplayName("사용자 수정 성공 - 주소만 변경")
    void updateUser_WithAddressOnly_ShouldReturnTrue() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setAddress("부산광역시 해운대구");
        
        // When
        boolean result = userService.updateUser(savedUser.getId(), request);
        
        // Then
        assertThat(result).isTrue();
        
        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getAddress()).isEqualTo("부산광역시 해운대구");
        assertThat(passwordEncoder.matches("password", updatedUser.getPassword())).isTrue(); // 암호는 변경되지 않음
    }
    
    @Test
    @DisplayName("사용자 수정 성공 - 암호와 주소 동시 변경")
    void updateUser_WithPasswordAndAddress_ShouldReturnTrue() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("oldpassword"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newpassword123");
        request.setAddress("대구광역시 중구");
        
        // When
        boolean result = userService.updateUser(savedUser.getId(), request);
        
        // Then
        assertThat(result).isTrue();
        
        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(passwordEncoder.matches("newpassword123", updatedUser.getPassword())).isTrue();
        assertThat(updatedUser.getAddress()).isEqualTo("대구광역시 중구");
    }
    
    @Test
    @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
    void updateUser_WithNonExistingId_ShouldReturnFalse() {
        // Given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newpassword123");
        
        // When
        boolean result = userService.updateUser(999L, request);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("사용자 수정 실패 - 수정할 내용 없음")
    void updateUser_WithNoUpdates_ShouldReturnFalse() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        UserUpdateRequest request = new UserUpdateRequest(); // 빈 요청
        
        // When
        boolean result = userService.updateUser(savedUser.getId(), request);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("사용자 삭제 성공 - 존재하는 사용자")
    void deleteUser_WithExistingId_ShouldReturnTrue() {
        // Given
        User user = new User();
        user.setAccount("testuser1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setName("홍길동");
        user.setResidentNumber("1234567890123");
        user.setPhoneNumber("01012345678");
        user.setAddress("서울시");
        User savedUser = userRepository.save(user);
        
        // When
        boolean result = userService.deleteUser(savedUser.getId());
        
        // Then
        assertThat(result).isTrue();
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }
    
    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
    void deleteUser_WithNonExistingId_ShouldReturnFalse() {
        // When
        boolean result = userService.deleteUser(999L);
        
        // Then
        assertThat(result).isFalse();
    }
}