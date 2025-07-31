package com.example.joinadmin.dto;

import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    
    @Size(min = 8, max = 100, message = "암호는 8자 이상 100자 이하여야 합니다")
    private String password;
    
    @Size(max = 500, message = "주소는 500자 이하여야 합니다")
    private String address;
    
    // 기본 생성자
    public UserUpdateRequest() {}
    
    // 생성자
    public UserUpdateRequest(String password, String address) {
        this.password = password;
        this.address = address;
    }
    
    // 업데이트할 필드가 있는지 확인
    public boolean hasUpdates() {
        return (password != null && !password.trim().isEmpty()) || 
               (address != null && !address.trim().isEmpty());
    }
    
    // 암호 업데이트 여부 확인
    public boolean hasPasswordUpdate() {
        return password != null && !password.trim().isEmpty();
    }
    
    // 주소 업데이트 여부 확인
    public boolean hasAddressUpdate() {
        return address != null && !address.trim().isEmpty();
    }
    
    // Getter & Setter
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "hasPassword=" + hasPasswordUpdate() +
                ", hasAddress=" + hasAddressUpdate() +
                '}';
    }
}