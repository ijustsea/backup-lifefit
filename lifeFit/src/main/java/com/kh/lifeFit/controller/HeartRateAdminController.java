package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogPageResponse;
import com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogSearchRequest;
import com.kh.lifeFit.service.heartRateService.HeartRateAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/heart-rate-log")
public class HeartRateAdminController {

    private final HeartRateAdminService heartRateAdminService;

    /**
     * 관리자용 시스템 로그 조회
     * (상단 통계 + 파티션 현황 + 하단 로그 리스트 통합)
     */
    @GetMapping("/log")
    public ResponseEntity<HeartLogPageResponse> getAdminLogDashboard(
            HeartLogSearchRequest searchRequest,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        // 서비스 호출해 통합 응답 DTO 반환하기
        HeartLogPageResponse response = heartRateAdminService.getAdminLogData(searchRequest, pageable);

        return ResponseEntity.ok(response);
    }
}
