package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartRateKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component // DbHeartRateProducer의 @Component 주석처리함
@RequiredArgsConstructor
public class KafkaHeartRateProducer implements HeartRateProducer {

    private final KafkaTemplate<String, HeartRateKafkaDto> kafkaTemplate;
    private static final String TOPIC = "heart-rate-topic";

    @Override
    public void send(HeartDataRequestDto dto, int age, Gender gender, String email){

        // kafka 전용 통합 dto
        HeartRateKafkaDto kafkaDto = new HeartRateKafkaDto(
                dto.userId(),
                dto.heartRate(),
                dto.measuredAt().toString(), // LocalDateTime -> String 변환
                age,
                gender,
                email
        );

        // userId를 key로 설정해서 전송 (동일 사용자는 동일 파티션으로 가서 순서 보장됨)
        String key = String.valueOf(dto.userId());
        try {
            kafkaTemplate.send(TOPIC, key, kafkaDto);
            log.info("Kafka Topic 발행 성공 : UserId={}, HeartRate={}", dto.userId(), dto.heartRate());
        } catch (Exception e) {
            log.error("Kafka 전송 실패 : {}", e.getMessage());
            // fallback 로직 수행 생각 중
        }
    }
}
