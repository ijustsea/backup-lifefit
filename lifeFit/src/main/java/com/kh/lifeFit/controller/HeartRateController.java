package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.heartData.MonitoringPage.HeartDataRequestDto;
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
    @GetMapping("/{userId}")
    public ResponseEntity<String> testHeartRate(@PathVariable Long userId){

        // 1. 서비스한테 "이 유저의 데이터 다 가져와!" 하고 시킴
        // (아직 서비스에 이 메서드가 없으니 빨간줄이 뜰 거예요. 곧 만들 겁니다!)
        // List<HeartDataResponseDto> dataList = heartRateService.getHeartRatesByUser(userId);

        // 2. 가져온 데이터 반환
        return ResponseEntity.ok("아직 서비스 로직 만들기 전입니다! (userId: " + userId + ")");

    }

}
