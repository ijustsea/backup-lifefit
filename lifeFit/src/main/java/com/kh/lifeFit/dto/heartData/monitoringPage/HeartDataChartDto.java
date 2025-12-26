package com.kh.lifeFit.dto.heartData.monitoringPage;

public record HeartDataChartDto(
        String time,    // 언제(X축), "2025-12-26 15:30"
        Integer value    // 얼마나(Y축), 평균 심박수
) {
}
