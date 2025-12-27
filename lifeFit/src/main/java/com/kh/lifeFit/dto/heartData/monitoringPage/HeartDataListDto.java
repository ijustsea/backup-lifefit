package com.kh.lifeFit.dto.heartData.monitoringPage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
public record HeartDataListDto(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime measuredAt, // 측정 시간
        int heartRate,            // 심박수
        int variation,            // 변화량 (DB에 저장된 값 그대로 사용)
        String timeAge,           // 경과 시간 "5분 전"
        HeartRateStatus status    // 상태 "정상" "경고" "위험"
        ) {

    public static HeartDataListDto from(HeartRateData heartRateData) {
        return new HeartDataListDto(
                heartRateData.getMeasuredAt(),
                heartRateData.getHeartRate(),
                heartRateData.getVariation(),
                calculateTimeAgo(heartRateData.getMeasuredAt()),
                heartRateData.getStatus()
        );
    }

    // 경과 시간 계산 로직 (최근 24시간 기준)
    public static String calculateTimeAgo(LocalDateTime measuredAt) {
        if (measuredAt == null) return "-";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(measuredAt, now);
        long seconds = duration.getSeconds();

        if(seconds < 60) {
            return " 방금 전";
        }else if(seconds < 3600) {
            return (seconds / 60) + "분 전";
        }else {
            return (seconds / 3600) + "시간 전";
        }
    }
}
