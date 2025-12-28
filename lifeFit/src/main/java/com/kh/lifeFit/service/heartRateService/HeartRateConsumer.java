package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.heartData.HeartRateAlert;
import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataListDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartRateKafkaDto;
import com.kh.lifeFit.monitor.SystemMonitor;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateAlertRepository;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartRateConsumer {

    private final HeartRateDataRepository heartRateDataRepository;
    private final HeartRateAlertRepository hearRateAlertRepository;
    private final SystemMonitor systemMonitor;

    @Transactional
    @KafkaListener(topics = "heart-rate-topic", groupId = "lifefit-group")
    public void consume(HeartRateKafkaDto dto) {
        long startTime = System.currentTimeMillis();
        // 가상 파티션 번호 (관리자 로그용)
        int virtualPartition = (int)(dto.userId() % 4);

        try {
            // String 날짜 -> LocalDateTime으로 복구
            LocalDateTime measuredAt = LocalDateTime.parse(dto.measuredAt());
            // 변화량 계산용 -> 최신 심박수 데이터 1건 조회 (기존 작성된 로직 이관)
            int lastHeartRate = heartRateDataRepository.findRecentDataList(dto.userId(), PageRequest.of(0, 1))
                    .stream()
                    .map(HeartDataListDto::heartRate)
                    .findFirst()
                    .orElse(dto.heartRate());
            // 엔티티 생성 -> 엔티티 내부에서 심박수 상태 판단
            HeartRateData heartRateData = HeartRateData.builder()
                    .userId(dto.userId())
                    .heartRate(dto.heartRate())
                    .measuredAt(measuredAt)
                    .variation(dto.heartRate() - lastHeartRate)
                    .age(dto.age())
                    .gender(dto.gender())
                    .build();
            // 원본 DB 저장
            heartRateDataRepository.save(heartRateData);
            // 심박수 '비정상' 상태 -> 알림 테이블에 저장
            if (heartRateData.getStatus().isAbnormal()){
                HeartRateAlert alert = new HeartRateAlert(heartRateData);
                hearRateAlertRepository.save(alert);
                log.info("⚠️ 이상 징후 감지! 알림 저장 완료: User={}", dto.userId());
            }
            // 성공 로그 기록
            long duration = System.currentTimeMillis() - startTime;
            systemMonitor.recordHeartRateLog(dto.userId(), dto.email(), virtualPartition, (int)duration, ProcessStatus.SUCCESS, null);
            log.debug("Kafka Topic 발행 성공 : UserId={}, HeartRate={}", dto.userId(), dto.heartRate());
        } catch (Exception e) {
            log.error("❌ 데이터 소비 중 오류 발생: {}", e.getMessage());
            systemMonitor.recordHeartRateLog(dto.userId(), dto.email(), virtualPartition, 0, ProcessStatus.FAIL_SERVER, e.getMessage());
            // 여기서 예외를 던지면 카프카가 메시지 처리에 실패한 것으로 간주하고 재시도 설정을 따름
            throw new RuntimeException(e);
        }
    }
}
