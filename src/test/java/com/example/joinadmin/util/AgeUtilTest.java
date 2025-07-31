package com.example.joinadmin.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class AgeUtilTest {
    
    @Test
    @DisplayName("나이 계산 - 1990년대 출생 남성")
    void calculateAge_Male1990s_ShouldReturnCorrectAge() {
        // Given - 1990년 1월 1일 출생 남성 (주민등록번호: 9001011234567)
        String residentNumber = "9001011234567";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = LocalDate.now().getYear() - 1990;
        if (LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), 1, 1))) {
            expectedAge--;
        }
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 - 1990년대 출생 여성")
    void calculateAge_Female1990s_ShouldReturnCorrectAge() {
        // Given - 1995년 6월 15일 출생 여성 (주민등록번호: 9506152345678)
        String residentNumber = "9506152345678";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = LocalDate.now().getYear() - 1995;
        LocalDate birthDate = LocalDate.of(1995, 6, 15);
        if (LocalDate.now().isBefore(birthDate.withYear(LocalDate.now().getYear()))) {
            expectedAge--;
        }
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 - 2000년대 출생 남성")
    void calculateAge_Male2000s_ShouldReturnCorrectAge() {
        // Given - 2000년 12월 31일 출생 남성 (주민등록번호: 0012313456789)
        String residentNumber = "0012313456789";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = LocalDate.now().getYear() - 2000;
        LocalDate birthDate = LocalDate.of(2000, 12, 31);
        if (LocalDate.now().isBefore(birthDate.withYear(LocalDate.now().getYear()))) {
            expectedAge--;
        }
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 - 2000년대 출생 여성")
    void calculateAge_Female2000s_ShouldReturnCorrectAge() {
        // Given - 2005년 3월 10일 출생 여성 (주민등록번호: 0503104567890)
        String residentNumber = "0503104567890";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = LocalDate.now().getYear() - 2005;
        LocalDate birthDate = LocalDate.of(2005, 3, 10);
        if (LocalDate.now().isBefore(birthDate.withYear(LocalDate.now().getYear()))) {
            expectedAge--;
        }
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 - 생일이 지나지 않은 경우")
    void calculateAge_BeforeBirthday_ShouldReturnCorrectAge() {
        // Given - 현재 날짜보다 늦은 생일을 가진 경우
        LocalDate today = LocalDate.now();
        LocalDate futureBirthday = today.plusDays(30); // 30일 후가 생일
        
        String year = String.format("%02d", futureBirthday.getYear() % 100);
        String month = String.format("%02d", futureBirthday.getMonthValue());
        String day = String.format("%02d", futureBirthday.getDayOfMonth());
        
        // 2000년대 출생으로 가정 (남성)
        String residentNumber = year + month + day + "3123456";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = today.getYear() - (2000 + Integer.parseInt(year)) - 1; // 생일이 지나지 않았으므로 -1
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 - 생일이 지난 경우")
    void calculateAge_AfterBirthday_ShouldReturnCorrectAge() {
        // Given - 현재 날짜보다 이른 생일을 가진 경우
        LocalDate today = LocalDate.now();
        LocalDate pastBirthday = today.minusDays(30); // 30일 전이 생일이었음
        
        String year = String.format("%02d", pastBirthday.getYear() % 100);
        String month = String.format("%02d", pastBirthday.getMonthValue());
        String day = String.format("%02d", pastBirthday.getDayOfMonth());
        
        // 2000년대 출생으로 가정 (여성)
        String residentNumber = year + month + day + "4123456";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = today.getYear() - (2000 + Integer.parseInt(year)); // 생일이 지났으므로 그대로
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 - 오늘이 생일인 경우")
    void calculateAge_TodayIsBirthday_ShouldReturnCorrectAge() {
        // Given - 오늘이 생일인 경우
        LocalDate today = LocalDate.now();
        
        String year = String.format("%02d", today.getYear() % 100);
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());
        
        // 2000년대 출생으로 가정 (남성)
        String residentNumber = year + month + day + "3123456";
        
        // When
        int age = AgeUtil.calculateAge(residentNumber);
        
        // Then
        int expectedAge = today.getYear() - (2000 + Integer.parseInt(year)); // 오늘이 생일이므로 정확한 나이
        assertThat(age).isEqualTo(expectedAge);
    }
    
    @Test
    @DisplayName("나이 계산 실패 - null 주민등록번호")
    void calculateAge_NullResidentNumber_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> AgeUtil.calculateAge(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주민등록번호는 13자리여야 합니다.");
    }
    
    @Test
    @DisplayName("나이 계산 실패 - 잘못된 길이의 주민등록번호")
    void calculateAge_InvalidLengthResidentNumber_ShouldThrowException() {
        // Given
        String shortResidentNumber = "123456789012"; // 12자리
        String longResidentNumber = "12345678901234"; // 14자리
        
        // When & Then
        assertThatThrownBy(() -> AgeUtil.calculateAge(shortResidentNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주민등록번호는 13자리여야 합니다.");
        
        assertThatThrownBy(() -> AgeUtil.calculateAge(longResidentNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주민등록번호는 13자리여야 합니다.");
    }
    
    @Test
    @DisplayName("나이 계산 실패 - 잘못된 성별 코드")
    void calculateAge_InvalidGenderCode_ShouldThrowException() {
        // Given
        String invalidGenderCode = "9001015123456"; // 성별코드 5 (유효하지 않음)
        
        // When & Then
        assertThatThrownBy(() -> AgeUtil.calculateAge(invalidGenderCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 주민등록번호 형식입니다.");
    }
    
    @Test
    @DisplayName("나이 계산 - 다양한 연령대 테스트")
    void calculateAge_VariousAges_ShouldReturnCorrectAges() {
        LocalDate currentDate = LocalDate.now();
        
        // 10대 (2010년 출생)
        String teenager = "1001013123456";
        int teenAge = AgeUtil.calculateAge(teenager);
        assertThat(teenAge).isBetween(13, 15); // 대략적인 범위
        
        // 20대 (2000년 출생)
        String twenties = "0001013123456";
        int twentyAge = AgeUtil.calculateAge(twenties);
        assertThat(twentyAge).isBetween(23, 25); // 대략적인 범위
        
        // 30대 (1990년 출생)
        String thirties = "9001011123456";
        int thirtyAge = AgeUtil.calculateAge(thirties);
        assertThat(thirtyAge).isBetween(33, 35); // 대략적인 범위
        
        // 40대 (1980년 출생)
        String forties = "8001011123456";
        int fortyAge = AgeUtil.calculateAge(forties);
        assertThat(fortyAge).isBetween(43, 45); // 대략적인 범위
    }
    
    @Test
    @DisplayName("나이 계산 - 윤년 생일 처리")
    void calculateAge_LeapYearBirth_ShouldReturnCorrectAge() {
        // Given - 윤년(2000년) 2월 29일 출생
        String leapYearBirth = "0002293123456";
        
        // When
        int age = AgeUtil.calculateAge(leapYearBirth);
        
        // Then
        int expectedAge = LocalDate.now().getYear() - 2000;
        LocalDate birthDate = LocalDate.of(2000, 2, 29);
        LocalDate currentYearBirthday = LocalDate.of(LocalDate.now().getYear(), 2, 28); // 평년에는 2월 28일
        
        if (LocalDate.now().isBefore(currentYearBirthday)) {
            expectedAge--;
        }
        
        assertThat(age).isEqualTo(expectedAge);
    }
}