package com.example.joinadmin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "계정은 필수입니다")
    @Size(min = 4, max = 50, message = "계정은 4자 이상 50자 이하여야 합니다")
    private String account;
    
    @Column(nullable = false)
    @NotBlank(message = "암호는 필수입니다")
    private String password;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "성명은 필수입니다")
    @Size(max = 50, message = "성명은 50자 이하여야 합니다")
    private String name;
    
    @Column(unique = true, nullable = false, length = 13)
    @NotBlank(message = "주민등록번호는 필수입니다")
    @Pattern(regexp = "\\d{13}", message = "주민등록번호는 13자리 숫자여야 합니다")
    private String residentNumber;
    
    @Column(nullable = false, length = 11)
    @NotBlank(message = "핸드폰번호는 필수입니다")
    @Pattern(regexp = "\\d{11}", message = "핸드폰번호는 11자리 숫자여야 합니다")
    private String phoneNumber;
    
    @Column(nullable = false, length = 500)
    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 500, message = "주소는 500자 이하여야 합니다")
    private String address;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    public User() {}
    
    // 생성자
    public User(String account, String password, String name, String residentNumber, String phoneNumber, String address) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.residentNumber = residentNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    // Getter & Setter
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
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}