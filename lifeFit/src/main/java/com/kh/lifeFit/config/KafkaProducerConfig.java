package com.kh.lifeFit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    // 심박수 데이터를 보낼 토픽 정의하기
    @Bean
    public NewTopic heartRateTopic() {
        return TopicBuilder.name("heart-rate-topic")
                .partitions(4) //
                .replicas(1)    // 로컬 테스트용, 복제본 1개
                .build();
    }
}
