package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartRateDataResponse;
import com.kh.lifeFit.service.heartRateService.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/heart-rate") // 공통 주소
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class HeartRateController {

    private final HeartRateService heartRateService;

    // 심박수 저장
    @PostMapping
    public ResponseEntity<Void> recordHeartRate(@RequestBody HeartDataRequestDto dto){
        // 서비스 호출
        heartRateService.record(dto);
        return ResponseEntity.ok().build(); // 응답body가 없기 때문에 반환형은 Void
    }

    // 심박수 조회
    // 대시보드 객체(통계+차트+리스트)를 반환
    @GetMapping("{userId}")
    public ResponseEntity<HeartRateDataResponse> getHeartRateDashboard(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "10") int limit){

        // 서비스 호출 -> 특정 사용자의 최근 심박수 N개를 가져오기
        HeartRateDataResponse response = heartRateService.getDashboardData(userId, limit);
        return ResponseEntity.ok(response);
    }

    // 랜덤 심박수 데이터 생성
    @PostMapping("/simulate/{userId}")
    public ResponseEntity<String> simulateHeartRate(@PathVariable Long userId){

        // 심박수 60~135 범위의 랜덤값 생성
        int randomHeartRate = (int) (Math.random() * 76) + 60;

        HeartDataRequestDto dto = new HeartDataRequestDto(
                userId,
                randomHeartRate,
                LocalDateTime.now()
        );

        // 서비스 호출
        heartRateService.record(dto);

        return ResponseEntity.ok(String.format("데이터 생성 성공: %d bpm (유저: %d)", randomHeartRate, userId));
    }

}
