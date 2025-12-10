package com.kh.lifeFit.dto.heartData;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;

import java.time.LocalDateTime;

public class HeartRateDataResponseDto {

    private int heartRate;              // 심박수
    private int avgHeartRate;           // 평균 심박수
    private int maxHeartRate;           // 최고 심박수
    private LocalDateTime measuredAt;   // 측정 시간

    private String elapsedTime;         // 경과 시간 "15분 전"
    private HeartRateStatus status;     // 상태 : 정상, 경고, 위험
}
