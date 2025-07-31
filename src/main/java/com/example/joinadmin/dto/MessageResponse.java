package com.example.joinadmin.dto;

public class MessageResponse {
    
    private boolean success;
    private String message;
    private Integer targetUserCount;
    private Integer successCount;
    private Integer failCount;
    
    // 기본 생성자
    public MessageResponse() {}
    
    // 전체 필드 생성자
    public MessageResponse(boolean success, String message, Integer targetUserCount, Integer successCount, Integer failCount) {
        this.success = success;
        this.message = message;
        this.targetUserCount = targetUserCount;
        this.successCount = successCount;
        this.failCount = failCount;
    }
    
    // 성공 응답 생성 메서드
    public static MessageResponse success(String message, Integer targetUserCount, Integer successCount, Integer failCount) {
        return new MessageResponse(true, message, targetUserCount, successCount, failCount);
    }
    
    // 실패 응답 생성 메서드
    public static MessageResponse failure(String message) {
        return new MessageResponse(false, message, null, null, null);
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
    
    public Integer getTargetUserCount() {
        return targetUserCount;
    }
    
    public void setTargetUserCount(Integer targetUserCount) {
        this.targetUserCount = targetUserCount;
    }
    
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public Integer getFailCount() {
        return failCount;
    }
    
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }
}