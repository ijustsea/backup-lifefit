package com.kh.lifeFit.dto.heartData.alertPage;

import org.springframework.data.domain.Page;

import java.time.LocalDate;

public record HeartRateAlertResponse(
        HeartAlertStatsDto stats,        // 상단 알림 통계
        Page<HeartAlertListDto> list,    // 하단 알림 상세 내역
        LocalDate appliedStartDate,      // [SSOT] 서비가 실제로 쿼리한 시작일
        LocalDate appliedEndDate         // [SSOT] 서비가 실제로 쿼리한 종료일
) {

}
