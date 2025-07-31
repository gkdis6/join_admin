package com.example.joinadmin.repository;

import com.example.joinadmin.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setAccount("testuser1");
        testUser.setPassword("encodedPassword");
        testUser.setName("홍길동");
        testUser.setResidentNumber("1234567890123");
        testUser.setPhoneNumber("01012345678");
        testUser.setAddress("서울특별시 강남구 테헤란로 123");
    }
    
    @Test
    @DisplayName("사용자 저장 및 조회")
    void save_AndFindById_ShouldWork() {
        // When
        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        
        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getAccount()).isEqualTo("testuser1");
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
    }
    
    @Test
    @DisplayName("계정으로 사용자 조회 - 존재하는 사용자")
    void findByAccount_WithExistingAccount_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        Optional<User> foundUser = userRepository.findByAccount("testuser1");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getAccount()).isEqualTo("testuser1");
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
        assertThat(foundUser.get().getResidentNumber()).isEqualTo("1234567890123");
    }
    
    @Test
    @DisplayName("계정으로 사용자 조회 - 존재하지 않는 사용자")
    void findByAccount_WithNonExistingAccount_ShouldReturnEmpty() {
        // When
        Optional<User> foundUser = userRepository.findByAccount("nonexistent");
        
        // Then
        assertThat(foundUser).isEmpty();
    }
    
    @Test
    @DisplayName("주민등록번호로 사용자 조회 - 존재하는 사용자")
    void findByResidentNumber_WithExistingNumber_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        Optional<User> foundUser = userRepository.findByResidentNumber("1234567890123");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getResidentNumber()).isEqualTo("1234567890123");
        assertThat(foundUser.get().getAccount()).isEqualTo("testuser1");
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
    }
    
    @Test
    @DisplayName("주민등록번호로 사용자 조회 - 존재하지 않는 사용자")
    void findByResidentNumber_WithNonExistingNumber_ShouldReturnEmpty() {
        // When
        Optional<User> foundUser = userRepository.findByResidentNumber("9999999999999");
        
        // Then
        assertThat(foundUser).isEmpty();
    }
    
    @Test
    @DisplayName("계정 중복 체크 - 존재하는 계정")
    void existsByAccount_WithExistingAccount_ShouldReturnTrue() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        boolean exists = userRepository.existsByAccount("testuser1");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("계정 중복 체크 - 존재하지 않는 계정")
    void existsByAccount_WithNonExistingAccount_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByAccount("nonexistent");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    @DisplayName("주민등록번호 중복 체크 - 존재하는 번호")
    void existsByResidentNumber_WithExistingNumber_ShouldReturnTrue() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        // When
        boolean exists = userRepository.existsByResidentNumber("1234567890123");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("주민등록번호 중복 체크 - 존재하지 않는 번호")
    void existsByResidentNumber_WithNonExistingNumber_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByResidentNumber("9999999999999");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    @DisplayName("계정과 주민등록번호 유니크 제약 조건 테스트")
    void uniqueConstraints_ShouldWork() {
        // Given - 첫 번째 사용자 저장
        entityManager.persistAndFlush(testUser);
        
        // When & Then - 동일한 계정으로 두 번째 사용자 저장 시도
        User duplicateAccountUser = new User();
        duplicateAccountUser.setAccount("testuser1"); // 중복 계정
        duplicateAccountUser.setPassword("password2");
        duplicateAccountUser.setName("김철수");
        duplicateAccountUser.setResidentNumber("9876543210987");
        duplicateAccountUser.setPhoneNumber("01087654321");
        duplicateAccountUser.setAddress("부산광역시");
        
        assertThatThrownBy(() -> {
            entityManager.persistAndFlush(duplicateAccountUser);
        }).hasMessageContaining("could not execute statement");
        
        // When & Then - 동일한 주민등록번호로 두 번째 사용자 저장 시도
        User duplicateResidentUser = new User();
        duplicateResidentUser.setAccount("testuser2");
        duplicateResidentUser.setPassword("password2");
        duplicateResidentUser.setName("김철수");
        duplicateResidentUser.setResidentNumber("1234567890123"); // 중복 주민등록번호
        duplicateResidentUser.setPhoneNumber("01087654321");
        duplicateResidentUser.setAddress("부산광역시");
        
        assertThatThrownBy(() -> {
            entityManager.persistAndFlush(duplicateResidentUser);
        }).hasMessageContaining("could not execute statement");
    }
    
    @Test
    @DisplayName("사용자 삭제")
    void delete_ShouldWork() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();
        
        // When
        userRepository.deleteById(userId);
        entityManager.flush();
        
        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }
    
    @Test
    @DisplayName("사용자 수정 - 타임스탬프 업데이트")
    void update_ShouldUpdateTimestamp() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        
        // When
        savedUser.setAddress("대구광역시 중구");
        User updatedUser = entityManager.persistAndFlush(savedUser);
        
        // Then
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
        assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
        assertThat(updatedUser.getAddress()).isEqualTo("대구광역시 중구");
    }
    
    @Test
    @DisplayName("사용자 개수 조회")
    void count_ShouldReturnCorrectCount() {
        // Given
        entityManager.persistAndFlush(testUser);
        
        User secondUser = new User();
        secondUser.setAccount("testuser2");
        secondUser.setPassword("password2");
        secondUser.setName("김철수");
        secondUser.setResidentNumber("9876543210987");
        secondUser.setPhoneNumber("01087654321");
        secondUser.setAddress("부산광역시");
        entityManager.persistAndFlush(secondUser);
        
        // When
        long count = userRepository.count();
        
        // Then
        assertThat(count).isEqualTo(2);
    }
}