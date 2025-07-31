package com.example.joinadmin.repository;

import com.example.joinadmin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 계정으로 사용자 조회
     * @param account 계정
     * @return 사용자 정보
     */
    Optional<User> findByAccount(String account);
    
    /**
     * 주민등록번호로 사용자 조회
     * @param residentNumber 주민등록번호
     * @return 사용자 정보
     */
    Optional<User> findByResidentNumber(String residentNumber);
    
    /**
     * 계정 중복 체크
     * @param account 계정
     * @return 존재 여부
     */
    boolean existsByAccount(String account);
    
    /**
     * 주민등록번호 중복 체크
     * @param residentNumber 주민등록번호
     * @return 존재 여부
     */
    boolean existsByResidentNumber(String residentNumber);
}