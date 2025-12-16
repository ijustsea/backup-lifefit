package com.kh.lifeFit.dto.heartData.monitoringPage;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
public record HeartDataListDto(
        LocalDateTime measuredAt, // 측정 시간
        String timeAge,           // 경과 시간 "5분 전"
        int heartRate,            // 심박수
        int variation,            // 변화량 (DB에 저장된 값 그대로 사용)
        HeartRateStatus status    // 상태 "정상" "경고" "위험"
        ) {

    public static HeartDataListDto from(HeartRateData heartRateData) {
        return new HeartDataListDto(
                heartRateData.getMeasuredAt(),
                calculateTimeAgo(heartRateData.getMeasuredAt()),
                heartRateData.getHeartRate(),
                heartRateData.getVariation(),
                heartRateData.getStatus()
        );
    }

    // 경과 시간 계산 로직 (최근 24시간 기준)
    private static String calculateTimeAgo(LocalDateTime measuredAt) {
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
