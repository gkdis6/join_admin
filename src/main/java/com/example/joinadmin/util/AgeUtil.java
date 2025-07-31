package com.example.joinadmin.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AgeUtil {
    
    /**
     * 주민등록번호로부터 나이를 계산합니다.
     * @param residentNumber 주민등록번호 13자리
     * @return 만 나이
     */
    public static int calculateAge(String residentNumber) {
        if (residentNumber == null || residentNumber.length() != 13) {
            throw new IllegalArgumentException("주민등록번호는 13자리여야 합니다.");
        }
        
        // 생년월일 추출
        String birthDateStr = residentNumber.substring(0, 6);
        String genderCode = residentNumber.substring(6, 7);
        
        // 세기 판단
        int century;
        if (genderCode.equals("1") || genderCode.equals("2")) {
            century = 1900; // 1900년대 출생
        } else if (genderCode.equals("3") || genderCode.equals("4")) {
            century = 2000; // 2000년대 출생
        } else {
            throw new IllegalArgumentException("올바르지 않은 주민등록번호 형식입니다.");
        }
        
        // 생년월일 파싱
        int year = century + Integer.parseInt(birthDateStr.substring(0, 2));
        int month = Integer.parseInt(birthDateStr.substring(2, 4));
        int day = Integer.parseInt(birthDateStr.substring(4, 6));
        
        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();
        
        // 만 나이 계산
        int age = currentDate.getYear() - birthDate.getYear();
        
        // 생일이 지나지 않았으면 1살 빼기
        if (currentDate.isBefore(birthDate.withYear(currentDate.getYear()))) {
            age--;
        }
        
        return age;
    }
}