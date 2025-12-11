package com.kh.lifeFit.dto.heartData.MonitoringPage;

public record HeartDataDashboardDto(
        int currentHeartRate,   // 현재 심박수
        int avgHeartRate,       // 평균 심박수
        int maxHeartRate        // 최고 심박수
) {
}

