package com.kh.lifeFit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본 유지 스레드 수
        executor.setMaxPoolSize(10); // 최대 스레드 수
        executor.setQueueCapacity(500); // 대기 큐 크기
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
