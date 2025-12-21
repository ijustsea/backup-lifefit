package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.healthData.HealthDataFilterRequest;
import com.kh.lifeFit.dto.healthData.HealthDataPageResponse;
import com.kh.lifeFit.dto.healthData.HealthDataSummaryRequest;
import com.kh.lifeFit.dto.healthData.HealthDataSummaryResponse;
import com.kh.lifeFit.service.healthDataService.HealthDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthDataController {

    private final HealthDataService healthDataService;

    @GetMapping("/api/admin/health")
    public ResponseEntity<HealthDataPageResponse> getHealthDataList (@ModelAttribute HealthDataFilterRequest filter, @PageableDefault Pageable pageable) {
        try {
            Pageable fixedPageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
            HealthDataPageResponse result = new HealthDataPageResponse(healthDataService.getHealthDataList(filter, fixedPageable));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("HealthData list 조회 중 예외 발생. filter={}, pageable={}", filter, pageable, e);
            return ResponseEntity.internalServerError().build(); // HTTP 상태 코드 500(Internal Server Error)만 내려주는 응답을 생성
        }
    }

    @GetMapping("/api/admin/health/summary")
    public ResponseEntity<HealthDataSummaryResponse> getHealthDataSummary (@ModelAttribute HealthDataSummaryRequest filter) {
        try {
            return ResponseEntity.ok(healthDataService.getHealthDataSummary(filter));
        } catch (Exception e) {
            log.error("HealthData Summary 조회 중 예외 발생. filter={}", filter, e);
            return ResponseEntity.internalServerError().build();
        }
    }

}