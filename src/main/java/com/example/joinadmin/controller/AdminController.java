package com.example.joinadmin.controller;

import com.example.joinadmin.dto.MessageRequest;
import com.example.joinadmin.dto.MessageResponse;
import com.example.joinadmin.dto.PagedResponse;
import com.example.joinadmin.dto.UserResponse;
import com.example.joinadmin.dto.UserUpdateRequest;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.service.MessageService;
import com.example.joinadmin.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "관리자 전용 API")
public class AdminController {
    
    private final UserService userService;
    private final MessageService messageService;
    
    @Autowired
    public AdminController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }
    
    /**
     * 회원 조회 API (페이징)
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준 (기본값: id)
     * @return 페이징된 회원 목록
     */
    @GetMapping("/users")
    public ResponseEntity<PagedResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        
        try {
            // 페이지 크기 제한 (최대 100)
            if (size > 100) {
                size = 100;
            }
            
            // 정렬 설정
            Sort sortBy = Sort.by(Sort.Direction.ASC, sort);
            Pageable pageable = PageRequest.of(page, size, sortBy);
            
            // 사용자 조회
            Page<User> userPage = userService.findAllUsers(pageable);
            
            // DTO 변환
            Page<UserResponse> responsePage = userPage.map(UserResponse::from);
            
            // 페이징 응답 생성
            PagedResponse<UserResponse> pagedResponse = PagedResponse.from(responsePage);
            
            return ResponseEntity.ok(pagedResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 특정 회원 조회 API
     * @param id 회원 ID
     * @return 회원 정보
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    /**
     * 회원 수정 API (암호, 주소만 수정 가능)
     * @param id 회원 ID
     * @param request 수정 요청 정보
     * @param bindingResult 유효성 검사 결과
     * @return 수정 결과
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            BindingResult bindingResult) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 1. 입력값 유효성 검사
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            response.put("success", false);
            response.put("message", "입력값 오류: " + errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // 2. 수정할 필드가 있는지 확인
        if (!request.hasUpdates()) {
            response.put("success", false);
            response.put("message", "수정할 정보가 없습니다. 암호 또는 주소를 입력해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // 3. 사용자 수정
        boolean success = userService.updateUser(id, request);
        
        if (success) {
            response.put("success", true);
            response.put("message", "회원 정보가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "회원을 찾을 수 없거나 수정에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * 회원 삭제 API
     * @param id 회원 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        boolean success = userService.deleteUser(id);
        
        if (success) {
            response.put("success", true);
            response.put("message", "회원이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "회원을 찾을 수 없거나 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * 연령대별 메시지 발송 API
     * @param request 메시지 발송 요청
     * @param bindingResult 유효성 검사 결과
     * @return 메시지 발송 결과
     */
    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody MessageRequest request,
            BindingResult bindingResult) {
        
        // 1. 입력값 유효성 검사
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            
            MessageResponse response = MessageResponse.failure("입력값 오류: " + errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // 2. 메시지 발송
        MessageResponse response = messageService.sendMessageByAge(request);
        
        // 3. 응답 처리
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
}