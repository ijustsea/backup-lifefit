package com.kh.lifeFit.monitor;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class SystemMonitor {

    private final HeartRateLogRepository heartRateLogRepository;

    /**
     * 로그 저장 로직
     * Propagation.REQUIRES_NEW: 비즈니스 로직이 실패하더라도 로그는 남아야 하므로 새 트랜잭션에서 실행
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 비즈니스 로직 실패해도 로그는 남도록 분리
    public void recordHeartRateLog(Long userId, String username, int partition, int durationMs, ProcessStatus status, String errorMsg) {
        HeartRateLog heartRateLog = HeartRateLog.builder()
                .userId(userId)
                .userName(username)
                .partitionNo(partition)
                .processingTimeMs(durationMs)
                .processStatus(status)
                .remarks(status.resolveRemarks(errorMsg))
                .build();

        heartRateLogRepository.save(heartRateLog);
    }

}
