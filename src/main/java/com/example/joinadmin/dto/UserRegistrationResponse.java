package com.example.joinadmin.dto;

public class UserRegistrationResponse {
    
    private boolean success;
    private String message;
    private Long userId;
    
    // 기본 생성자
    public UserRegistrationResponse() {}
    
    // 성공 응답용 생성자
    public UserRegistrationResponse(boolean success, String message, Long userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
    }
    
    // 실패 응답용 생성자
    public UserRegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.userId = null;
    }
    
    // 성공 응답 팩토리 메서드
    public static UserRegistrationResponse success(Long userId) {
        return new UserRegistrationResponse(true, "회원가입이 성공적으로 완료되었습니다.", userId);
    }
    
    // 실패 응답 팩토리 메서드
    public static UserRegistrationResponse failure(String message) {
        return new UserRegistrationResponse(false, message);
    }
    
    // Getter & Setter
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "UserRegistrationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                '}';
    }
}