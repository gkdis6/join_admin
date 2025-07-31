package com.example.joinadmin.dto;

public class LoginResponse {
    
    private boolean success;
    private String message;
    private String token;
    private Long userId;
    
    // 기본 생성자
    public LoginResponse() {}
    
    // 성공 응답 생성자
    public LoginResponse(boolean success, String message, String token, Long userId) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userId = userId;
    }
    
    // 실패 응답 생성자
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.token = null;
        this.userId = null;
    }
    
    // 성공 응답 생성 메서드
    public static LoginResponse success(String token, Long userId) {
        return new LoginResponse(true, "로그인이 성공적으로 완료되었습니다.", token, userId);
    }
    
    // 실패 응답 생성 메서드
    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message);
    }
    
    // Getter and Setter
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}