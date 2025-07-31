package com.example.joinadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MessageRequest {
    
    @NotNull(message = "최소 연령은 필수입니다")
    @Positive(message = "최소 연령은 양수여야 합니다")
    private Integer minAge;
    
    @NotNull(message = "최대 연령은 필수입니다")
    @Positive(message = "최대 연령은 양수여야 합니다")
    private Integer maxAge;
    
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String message;
    
    // 기본 생성자
    public MessageRequest() {}
    
    // 전체 필드 생성자
    public MessageRequest(Integer minAge, Integer maxAge, String message) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.message = message;
    }
    
    // Getter and Setter
    public Integer getMinAge() {
        return minAge;
    }
    
    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }
    
    public Integer getMaxAge() {
        return maxAge;
    }
    
    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}