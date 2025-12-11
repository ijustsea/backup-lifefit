package com.kh.lifeFit.dto.heartData.MonitoringPage;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;

import java.time.LocalDateTime;

public record HeartDataListDto(
        LocalDateTime measuredAt, // 측정 시간
        int heartRate,            // 심박수
        int variation,            // 변화량 (DB에 저장된 값 그대로 사용)
        HeartRateStatus status    // 상태 "정상" "경고" "위험"
        ) {
}
