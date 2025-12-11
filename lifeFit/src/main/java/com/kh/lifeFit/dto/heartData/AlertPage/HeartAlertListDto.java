package com.kh.lifeFit.dto.heartData.AlertPage;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;

import java.time.LocalDateTime;

public record HeartAlertListDto(
        LocalDateTime timestamp, // 수신 시간
        int heartRate,           // 심박수
        HeartRateStatus status   // 상태 "경고" "위험"
) {

    // Jackson 라이브러리는 get으로 시작하는 메서드를 보면
    // 자동으로 JSON 필드로 추가해준다.
    public String getHeartRateStatusText(){
        return status.getDisplayName(); // "경고" "위험" 한글로 반환
    }

    public String getHeartRateStatusColor(){
        return status.getColorCode(); // 색상코드 반환
    }
}
