package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartRateDataMockRunner {

    private final HeartRateService heartRateService;
    private static final int DELAYMS = 100;

    /**
     * 0.1초(100ms)마다 랜덤 데이터 생성
     * 결과적으로 초당 10건(10 TPS)의 부하를 생성합니다.
     */
    @Scheduled(fixedDelay = DELAYMS)
    public void generateHeartRateData() {
        // 랜덤 데이터 생성 유저 + 심박수
        Long randomUserId = ThreadLocalRandom.current().nextLong(1, 4);
        int randomHeartRate = ThreadLocalRandom.current().nextInt(60, 161);

        // 가상 이름
        String mockUserName = "MockUser_" + randomUserId.toString();

        HeartDataRequestDto dto = new HeartDataRequestDto(
                randomUserId,
                randomHeartRate,
                LocalDateTime.now()
        );

        try {
            // 실제 서비스 로직 호출
            heartRateService.record(randomUserId, mockUserName, dto);
            // log.info("데이터 생성 성공: 유저 {}, 심박수 {}", randomUserId, randomHeartRate);
        }catch (Exception e){
            // 실패 시 로그만 남기고 멈추지 않음 (FAIL_SERVER 등 테스트용)
            log.error("데이터 생성 중 오류 발생: {}", e.getMessage());
        }

    }

}
