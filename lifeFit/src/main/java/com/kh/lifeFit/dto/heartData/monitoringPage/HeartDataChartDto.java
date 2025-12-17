package com.kh.lifeFit.dto.heartData.monitoringPage;

import java.time.LocalDateTime;

public record HeartDataChartDto(
        LocalDateTime time,  // 언제(X축)
        int value            // 얼마나(Y축)
) {
}
