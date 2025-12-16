package com.kh.lifeFit.dto.heartData.monitoringPage;

import java.time.LocalDateTime;

public record HeartDataChartDto(
        LocalDateTime time,
        int value
) {
}
