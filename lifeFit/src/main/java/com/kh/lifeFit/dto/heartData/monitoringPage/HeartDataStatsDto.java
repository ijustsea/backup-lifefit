package com.kh.lifeFit.dto.heartData.monitoringPage;

public record HeartDataStatsDto(
        Integer currentHeartRate, // 현재 심박수
        Integer avgHeartRate,     // 평균 심박수 (Doubled과 int는 소수점(int는 소수점은 버려서 안됨))
        Integer maxHeartRate      // 최대 심박수
) {
}
