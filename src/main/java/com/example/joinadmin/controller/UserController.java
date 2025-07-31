package com.example.joinadmin.controller;

import com.example.joinadmin.dto.UserRegistrationRequest;
import com.example.joinadmin.dto.UserRegistrationResponse;
import com.example.joinadmin.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * API 상태 확인용 엔드포인트
     * @return 상태 응답
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User API is running");
    }
}