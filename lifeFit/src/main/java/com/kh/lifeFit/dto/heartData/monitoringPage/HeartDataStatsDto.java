package com.kh.lifeFit.dto.heartData.monitoringPage;

public record HeartDataStatsDto(
        Integer currentHeartRate, // 현재 심박수
        Double avgHeartRate,      // 평균 심박수 (소수점 가능)
        Integer maxHeartRate      // 최대 심박수
) {
}
