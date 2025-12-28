package com.kh.lifeFit.dto.heartData.monitoringPage;

import com.kh.lifeFit.domain.common.Gender;

public record HeartRateKafkaDto(
        Long userId,
        int heartRate,
        String measuredAt, // JSON 직렬화를 위해 String
        int age,
        Gender gender,
        String email
) {
}
