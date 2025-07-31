package com.example.joinadmin.service;

import com.example.joinadmin.dto.MessageRequest;
import com.example.joinadmin.dto.MessageResponse;
import com.example.joinadmin.entity.User;
import com.example.joinadmin.repository.UserRepository;
import com.example.joinadmin.util.AgeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    
    // API 호출 제한 관리
    private static final int KAKAO_RATE_LIMIT = 100; // 1분당 100회
    private static final int SMS_RATE_LIMIT = 500; // 1분당 500회
    private static final String KAKAO_API_URL = "http://localhost:8081/kakaotalk-messages";
    private static final String SMS_API_URL = "http://localhost:8082/sms";
    
    @Autowired
    public MessageService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 연령대별 메시지 발송
     * @param request 메시지 발송 요청
     * @return 메시지 발송 결과
     */
    public MessageResponse sendMessageByAge(MessageRequest request) {
        try {
            // 1. 입력값 검증
            if (request.getMinAge() > request.getMaxAge()) {
                return MessageResponse.failure("최소 연령이 최대 연령보다 클 수 없습니다.");
            }
            
            // 2. 연령대에 해당하는 사용자 조회
            List<User> allUsers = userRepository.findAll();
            List<User> targetUsers = allUsers.stream()
                    .filter(user -> {
                        try {
                            int age = AgeUtil.calculateAge(user.getResidentNumber());
                            return age >= request.getMinAge() && age <= request.getMaxAge();
                        } catch (Exception e) {
                            // 주민등록번호 오류가 있는 사용자는 제외
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            
            if (targetUsers.isEmpty()) {
                return MessageResponse.success("메시지 발송이 완료되었습니다.", 0, 0, 0);
            }
            
            // 3. 메시지 발송 (비동기 처리)
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            
            // 카카오톡 API 제한을 고려하여 배치 처리
            CompletableFuture<Void> sendingFuture = CompletableFuture.runAsync(() -> {
                for (User user : targetUsers) {
                    String personalizedMessage = String.format("%s님, 안녕하세요. 현대 오토에버입니다. %s", 
                            user.getName(), request.getMessage());
                    
                    // 카카오톡 메시지 발송 시도
                    boolean kakaoSuccess = sendKakaoMessage(user.getPhoneNumber(), personalizedMessage);
                    
                    if (kakaoSuccess) {
                        successCount.incrementAndGet();
                    } else {
                        // 카카오톡 실패 시 SMS 발송
                        boolean smsSuccess = sendSmsMessage(user.getPhoneNumber(), personalizedMessage);
                        if (smsSuccess) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    }
                    
                    // 속도 제한을 위한 대기 (실제로는 더 정교한 제한 필요)
                    try {
                        Thread.sleep(50); // 50ms 대기 (초당 20회 제한)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            
            // 최대 10초 대기
            try {
                sendingFuture.get(java.util.concurrent.TimeUnit.SECONDS.toMillis(10), 
                                java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                // 타임아웃 또는 오류 발생 시 현재까지의 결과 반환
            }
            
            return MessageResponse.success(
                    "메시지 발송이 완료되었습니다.",
                    targetUsers.size(),
                    successCount.get(),
                    failCount.get()
            );
            
        } catch (Exception e) {
            return MessageResponse.failure("메시지 발송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 카카오톡 메시지 발송
     * @param phone 전화번호
     * @param message 메시지 내용
     * @return 발송 성공 여부
     */
    private boolean sendKakaoMessage(String phone, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth("autoever", "1234");
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("phone", formatPhoneNumber(phone));
            requestBody.put("message", message);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            // 로그 출력 (운영환경에서는 적절한 로깅 프레임워크 사용)
            System.err.println("카카오톡 메시지 발송 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * SMS 메시지 발송
     * @param phone 전화번호
     * @param message 메시지 내용
     * @return 발송 성공 여부
     */
    private boolean sendSmsMessage(String phone, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth("autoever", "5678");
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("message", message);
            
            String url = SMS_API_URL + "?phone=" + formatPhoneNumber(phone);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            return response.getStatusCode() == HttpStatus.OK && 
                   "OK".equals(response.getBody().get("result"));
            
        } catch (Exception e) {
            // 로그 출력 (운영환경에서는 적절한 로깅 프레임워크 사용)
            System.err.println("SMS 메시지 발송 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 전화번호 형식 변환 (01012345678 -> 010-1234-5678)
     * @param phone 전화번호
     * @return 형식화된 전화번호
     */
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        
        return phone.substring(0, 3) + "-" + 
               phone.substring(3, 7) + "-" + 
               phone.substring(7, 11);
    }
}