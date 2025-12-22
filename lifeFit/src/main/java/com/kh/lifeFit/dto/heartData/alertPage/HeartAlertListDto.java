package com.kh.lifeFit.dto.heartData.alertPage;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;

import java.time.LocalDateTime;

public record HeartAlertListDto(
        LocalDateTime timestamp, // 수신 시간
        int heartRate,           // 심박수
        HeartRateStatus status   // 상태 "경고" "위험"
) {

    public String getHeartRateStatusText(){
        return status.getDisplayName(); // "경고" "위험" 한글로 반환
    }

    public String getHeartRateStatusColor(){
        return status.getColorCode(); // 색상코드 반환
    }
}
