package com.kh.lifeFit.dto.heartData.monitoringPage;

import java.time.LocalDateTime;

public record HeartDataRequestDto(
        Long userId,              // 누구인지
        int heartRate,            // 심박수 몇인지
        LocalDateTime measuredAt  // 언제 측정됐는지
) {
}
