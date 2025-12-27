package com.kh.lifeFit.controller;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartRateAlertResponse;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartRateDataResponse;
import com.kh.lifeFit.jwt.CustomUserDetails;
import com.kh.lifeFit.service.heartRateService.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/heart-rate") // 공통 주소
@RequiredArgsConstructor
public class HeartRateController {

    private final HeartRateService heartRateService;

    // 심박수 저장
    @PostMapping
    public ResponseEntity<Void> recordHeartRate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody HeartDataRequestDto dto){
        // 서비스 호출
        // 클라이언트가 보낸 dto.getUserId()는 무시하고, 토큰의 ID를 강제로 꽂아넣음
        heartRateService.record(
                customUserDetails.getUserId(),
                customUserDetails.getAge(),
                customUserDetails.getGender(),
                customUserDetails.getEmail(),
                dto
        );
        return ResponseEntity.ok().build(); // 응답body가 없기 때문에 반환형은 Void
    }

    /**
     * 1. 실시간 심박수 대시보드 데이터 조회 (본인 데이터만 조회)
     * 통계 + 차트 + 리스트 -> 객체로 반환
     * URL : GET /api/heart-rate/me
     */
    // 심박수 조회
    @GetMapping("/me")
    public ResponseEntity<HeartRateDataResponse> getHeartRateDashboard(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, // JWT에서 추출
            @RequestParam(required = false, defaultValue = "10") int limit){

        // customUserDetails(JWT 토큰)에서 로그인한 사용자의 고유 ID를 가져온다
        Long userId = customUserDetails.getUserId();
        int age = customUserDetails.getAge();
        Gender gender = customUserDetails.getGender();

        // 서비스 호출 -> 특정 사용자의 최근 심박수 N개를 가져오기
        // 위에서 추출한 userId와 클라이언트가 요청한 limut를 서비스에 전달
        HeartRateDataResponse response = heartRateService.getDashboardData(userId, age, gender, limit);

        return ResponseEntity.ok(response);
    }

    /**
     * 2. 심박수 알림 내역 조회 (필터링, 페이징 포함)
     */
    @GetMapping("/alert")
    public ResponseEntity<HeartRateAlertResponse> getAlert(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HeartAlertSearchRequest request,
            Pageable pageable
    ){
        return ResponseEntity.ok(heartRateService.getAlertData(customUserDetails.getUserId(), request, pageable));
    }


}
