package com.kh.lifeFit.dto.heartData.alertPage;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;

public record HeartAlertStatsDto(
        Long totalCount,                // 총 알림 건수
        Long cautionCount,              // 경고 등급 건수
        Long dangerCount,               // 위험 등급 건수
        HeartRateStatus cautionStatus,  // 경고 상태 Enum 객체 전체
        HeartRateStatus dangerStatus    // 위험 상태 Enum 객체 전체
){

    public HeartAlertStatsDto(Long cautionCount, Long dangerCount){
        this(
                (cautionCount != null ? cautionCount : 0L) + (dangerCount != null ? dangerCount : 0L),
                cautionCount,
                dangerCount,
                HeartRateStatus.CAUTION,
                HeartRateStatus.DANGER
        );
    }
}
