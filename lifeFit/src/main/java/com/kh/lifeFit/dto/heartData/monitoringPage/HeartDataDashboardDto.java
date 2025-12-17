package com.kh.lifeFit.dto.heartData.monitoringPage;

import lombok.Builder;

@Builder
public record HeartDataDashboardDto(
        int currentHeartRate,   // 현재 심박수
        int avgHeartRate,       // 평균 심박수
        int maxHeartRate        // 최고 심박수
) {
}

