package com.example.joinadmin.service;

import com.example.joinadmin.dto.LoginRequest;
import com.example.joinadmin.dto.LoginResponse;
import com.example.joinadmin.dto.UserRegistrationRequest;
import com.example.joinadmin.dto.UserRegistrationResponse;
import com.example.joinadmin.dto.UserUpdateRequest;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.repository.UserRepository;
import com.example.joinadmin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
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
     * 로그인 처리
     * @param request 로그인 요청 정보
     * @return 로그인 응답
     */
    public LoginResponse loginUser(LoginRequest request) {
        try {
            // 1. 계정으로 사용자 조회
            User user = userRepository.findByAccount(request.getAccount()).orElse(null);
            if (user == null) {
                return LoginResponse.failure("계정 또는 암호가 일치하지 않습니다.");
            }
            
            // 2. 암호 검증
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return LoginResponse.failure("계정 또는 암호가 일치하지 않습니다.");
            }
            
            // 3. JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getAccount(), user.getId());
            
            return LoginResponse.success(token, user.getId());
            
        } catch (Exception e) {
            return LoginResponse.failure("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
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
    
    // === 관리자 전용 메서드 ===
    
    /**
     * 모든 사용자 조회 (페이징)
     * @param pageable 페이징 정보
     * @return 페이징된 사용자 목록
     */
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * 사용자 정보 수정 (암호, 주소만)
     * @param id 사용자 ID
     * @param request 수정 요청 정보
     * @return 수정 성공 여부
     */
    public boolean updateUser(Long id, UserUpdateRequest request) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return false;
            }
            
            // 수정할 필드가 없으면 false 반환
            if (!request.hasUpdates()) {
                return false;
            }
            
            // 암호 수정
            if (request.hasPasswordUpdate()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // 주소 수정
            if (request.hasAddressUpdate()) {
                user.setAddress(request.getAddress());
            }
            
            userRepository.save(user);
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 사용자 삭제
     * @param id 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteUser(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}