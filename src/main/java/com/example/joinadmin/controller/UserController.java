package com.example.joinadmin.controller;

import com.example.joinadmin.dto.LoginRequest;
import com.example.joinadmin.dto.LoginResponse;
import com.example.joinadmin.dto.UserDetailResponse;
import com.example.joinadmin.dto.UserRegistrationRequest;
import com.example.joinadmin.dto.UserRegistrationResponse;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 회원가입 API
     * @param request 회원가입 요청 정보
     * @param bindingResult 유효성 검사 결과
     * @return 회원가입 응답
     */
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request,
            BindingResult bindingResult) {
        
        // 1. 입력값 유효성 검사
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            UserRegistrationResponse response = UserRegistrationResponse.failure("입력값 오류: " + errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // 2. 회원가입 처리
        UserRegistrationResponse response = userService.registerUser(request);
        
        // 3. 응답 처리
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 로그인 API
     * @param request 로그인 요청 정보
     * @param bindingResult 유효성 검사 결과
     * @return 로그인 응답
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult) {
        
        // 1. 입력값 유효성 검사
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            LoginResponse response = LoginResponse.failure("입력값 오류: " + errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // 2. 로그인 처리
        LoginResponse response = userService.loginUser(request);
        
        // 3. 응답 처리
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * 사용자 상세정보 조회 API (로그인 사용자 본인만)
     * @param authentication Spring Security 인증 정보
     * @return 사용자 상세정보 응답
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> getUserDetail(Authentication authentication) {
        try {
            // JWT 필터에서 설정한 userId를 가져옴
            Long userId = (Long) authentication.getDetails();
            
            // 사용자 정보 조회
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // 응답 생성 (주소는 가장 큰 단위의 행정구역만 포함)
            UserDetailResponse response = new UserDetailResponse(user);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}