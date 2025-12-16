package com.kh.lifeFit.dto.heartData.alertPage;

public record HeartAlertStatsDto(
        Long totalCount,    // 총 알림 건수
        Long cautionCount,  // 경고 등급 건수
        Long dangerCount    // 위험 등급 건수
){

    public HeartAlertStatsDto(Long cautionCount, Long dangerCount){

        this(cautionCount + dangerCount, cautionCount, dangerCount);
    }
}
