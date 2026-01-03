package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartRateKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component // DbHeartRateProducer의 @Component 주석처리함
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaHeartRateProducer implements HeartRateProducer {

    private final KafkaTemplate<String, HeartRateKafkaDto> kafkaTemplate;
    private static final String TOPIC = "heart-rate-topic";

    @Override
    public void send(HeartDataRequestDto dto, int age, Gender gender, String email){

        // 메서드 진입 로그 (이거 안 찍히면 호출 자체가 X)
        log.info("[kafka Attempt] Sending message to topic: {}, userId: {}", TOPIC, dto.userId());

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

        kafkaTemplate.send(TOPIC, key, kafkaDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[Kafka Success] Offset: {}", result.getRecordMetadata().offset());
            } else {
                // 여기가 핵심입니다. 인증 실패나 권한 에러가 이 로그에 찍힙니다.
                log.error("[Kafka Failure] {}", ex.getMessage());
            }
        });
    }
}
