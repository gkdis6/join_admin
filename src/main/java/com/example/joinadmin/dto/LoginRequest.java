package com.example.joinadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    
    @NotBlank(message = "계정은 필수입니다")
    @Size(min = 4, max = 50, message = "계정은 4자 이상 50자 이하여야 합니다")
    private String account;
    
    @NotBlank(message = "암호는 필수입니다")
    @Size(min = 8, max = 100, message = "암호는 8자 이상 100자 이하여야 합니다")
    private String password;
    
    // 기본 생성자
    public LoginRequest() {}
    
    // 전체 필드 생성자
    public LoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }
    
    // Getter and Setter
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}