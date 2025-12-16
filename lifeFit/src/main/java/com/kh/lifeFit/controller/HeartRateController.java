package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.service.heartRateService.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/heart-rates") // 공통 주소
@RequiredArgsConstructor
public class HeartRateController {

    private final HeartRateService heartRateService;

    // 심박수 저장
    @PostMapping
    public ResponseEntity<String> testHeartRate(@RequestBody HeartDataRequestDto dto){
        // 서비스 호출
        heartRateService.record(dto);
        return ResponseEntity.ok("심박수 데이터 수신 성공.");
    }

    // 심박수 조회
    //@GetMapping

}
