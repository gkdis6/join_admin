package com.example.joinadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequest {
    
    @NotBlank(message = "계정은 필수입니다")
    @Size(min = 4, max = 50, message = "계정은 4자 이상 50자 이하여야 합니다")
    private String account;
    
    @NotBlank(message = "암호는 필수입니다")
    @Size(min = 8, max = 100, message = "암호는 8자 이상 100자 이하여야 합니다")
    private String password;
    
    @NotBlank(message = "성명은 필수입니다")
    @Size(max = 50, message = "성명은 50자 이하여야 합니다")
    private String name;
    
    @NotBlank(message = "주민등록번호는 필수입니다")
    @Pattern(regexp = "\\d{13}", message = "주민등록번호는 13자리 숫자여야 합니다")
    private String residentNumber;
    
    @NotBlank(message = "핸드폰번호는 필수입니다")
    @Pattern(regexp = "\\d{11}", message = "핸드폰번호는 11자리 숫자여야 합니다")
    private String phoneNumber;
    
    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 500, message = "주소는 500자 이하여야 합니다")
    private String address;
    
    // 기본 생성자
    public UserRegistrationRequest() {}
    
    // 생성자
    public UserRegistrationRequest(String account, String password, String name, 
                                 String residentNumber, String phoneNumber, String address) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.residentNumber = residentNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    // Getter & Setter
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getResidentNumber() {
        return residentNumber;
    }
    
    public void setResidentNumber(String residentNumber) {
        this.residentNumber = residentNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}