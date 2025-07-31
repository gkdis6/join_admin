package com.example.joinadmin.service;

import com.example.joinadmin.dto.UserRegistrationRequest;
import com.example.joinadmin.dto.UserRegistrationResponse;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 회원가입 처리
     * @param request 회원가입 요청 정보
     * @return 회원가입 응답
     */
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        try {
            // 1. 계정 중복 체크
            if (userRepository.existsByAccount(request.getAccount())) {
                return UserRegistrationResponse.failure("이미 존재하는 계정입니다.");
            }
            
            // 2. 주민등록번호 중복 체크
            if (userRepository.existsByResidentNumber(request.getResidentNumber())) {
                return UserRegistrationResponse.failure("이미 등록된 주민등록번호입니다.");
            }
            
            // 3. 사용자 엔티티 생성
            User user = new User();
            user.setAccount(request.getAccount());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호 해싱
            user.setName(request.getName());
            user.setResidentNumber(request.getResidentNumber());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAddress(request.getAddress());
            
            // 4. 사용자 저장
            User savedUser = userRepository.save(user);
            
            return UserRegistrationResponse.success(savedUser.getId());
            
        } catch (Exception e) {
            return UserRegistrationResponse.failure("회원가입 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 계정으로 사용자 조회
     * @param account 계정
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public User findByAccount(String account) {
        return userRepository.findByAccount(account).orElse(null);
    }
    
    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}