package com.example.joinadmin.dto;

import com.example.joinadmin.entity.User;

import java.time.LocalDateTime;

public class UserDetailResponse {
    
    private Long id;
    private String account;
    private String name;
    private String residentNumber;
    private String phoneNumber;
    private String address; // 가장 큰 단위의 행정구역만 포함
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    public UserDetailResponse() {}
    
    // User 엔티티로부터 생성하는 생성자
    public UserDetailResponse(User user) {
        this.id = user.getId();
        this.account = user.getAccount();
        this.name = user.getName();
        this.residentNumber = user.getResidentNumber();
        this.phoneNumber = user.getPhoneNumber();
        this.address = extractMainAdministrativeRegion(user.getAddress());
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
    
    /**
     * 주소에서 가장 큰 단위의 행정구역을 추출합니다.
     * 예: "서울특별시 강남구 테헤란로 123" -> "서울특별시"
     * @param fullAddress 전체 주소
     * @return 가장 큰 단위의 행정구역
     */
    private String extractMainAdministrativeRegion(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            return "";
        }
        
        String[] addressParts = fullAddress.trim().split(" ");
        if (addressParts.length > 0) {
            String firstPart = addressParts[0];
            
            // 특별시, 광역시, 도, 특별자치도, 특별자치시 등으로 끝나는 경우
            if (firstPart.endsWith("특별시") || 
                firstPart.endsWith("광역시") || 
                firstPart.endsWith("도") || 
                firstPart.endsWith("특별자치도") || 
                firstPart.endsWith("특별자치시")) {
                return firstPart;
            }
            
            // 시, 군으로 끝나는 경우 (도가 없는 특별한 경우)
            if (firstPart.endsWith("시") || firstPart.endsWith("군")) {
                return firstPart;
            }
        }
        
        // 패턴에 맞지 않는 경우 첫 번째 공백 이전까지 반환
        return addressParts[0];
    }
    
    // Getter and Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}