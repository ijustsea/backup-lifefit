package com.kh.lifeFit.dto.heartData.MonitoringPage;

import java.time.LocalDateTime;

public record HeartDataChartDto(
        LocalDateTime time,
        int value
) {
}
