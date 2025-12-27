package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.common.Gender;
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
    private static final int INTERVAL_MS = 100; // 2025.12.25 초당 10건(10 TPS)하기 위해 100ms로 설정

    /**
     * 1초(100ms)마다 랜덤 데이터 생성
     * 결과적으로 초당 10건(10 TPS)의 부하를 생성
     */
    @Scheduled(fixedDelay = INTERVAL_MS)
    public void generateHeartRateData() {
        // 실제 DB 유저 ID, 배열에 담기()
        Long[] testUserIds = {6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L};
        // 유저ID 담긴 배열에서 랜덤으로 인덱스 하나 추출하기
        //int randomIndex = ThreadLocalRandom.current().nextInt(testUserIds.length);
        //Long targetUserId = testUserIds[randomIndex];
        Long targetUserId = testUserIds[ThreadLocalRandom.current().nextInt(testUserIds.length)];

        // 가상 유저ID
        //Long randomUserId = ThreadLocalRandom.current().nextLong(1, 4);

        // SQL 데이터와 이메일 패턴 일치시키기 (ID 6 -> test_01, ID 15 -> test_10)
        // 유저 번호를 추출하기 위해 index + 1 등을 활용하거나 직접 조합
        // (targetUserId - 5)를 통해 1~10 숫자를 만들고 %02d로 두 자릿수 포맷팅
        String mockEmail = "test_" + String.format("%02d", (targetUserId - 5)) + "@lifefit.com";

        // 가상 랜덤 데이터 생성
        // 심박수 랜덤 생성 (60 ~ 160)
        int randomHeartRate = ThreadLocalRandom.current().nextInt(60, 161);

        // 나이 (20~60)
        int mockAge = ThreadLocalRandom.current().nextInt(20, 61);

        // 선택된 targetUserId 기준으로 성별
        Gender mockGender = (targetUserId % 2 == 0) ? Gender.MALE : Gender.FEMALE;

        // 가상 데이터 DTO 만들기
        HeartDataRequestDto dto = new HeartDataRequestDto(
                targetUserId,
                randomHeartRate,
                LocalDateTime.now()
        );

        try {
            // 실제 서비스 로직 호출
            heartRateService.record(targetUserId, mockAge, mockGender, mockEmail, dto);
            // log.info("데이터 생성 성공: 유저ID {}, 이메일 {}, 심박수 {}", targetUserId, mockEmail, randomHeartRate);
        }catch (Exception e){
            // 실패 시 로그만 남기고 멈추지 않음 (FAIL_SERVER 등 테스트용)

        }

    }

}
