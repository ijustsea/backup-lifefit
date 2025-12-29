package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartRateDataMockRunner {

    private final HeartRateService heartRateService;
    private final UserRepository userRepository; // 모든 사용자 가져오기

    private static final int INTERVAL_MS = 1000; // 2025.12.25 초당 10건(10 TPS)하기 위해 100ms로 설정

    /**
     * 1초(1000ms)마다 랜덤 데이터 생성
     * 결과적으로 초당 10건(10 TPS)의 부하를 생성
     */
    @Scheduled(fixedDelay = INTERVAL_MS)
    public void generateHeartRateData() {
        // 실제 DB 유저 ID, 배열에 담기()
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            log.warn("데이터를 생성할 사용자가 DB에 존재하지 않습니다.");
            return;
        }

        // 모든 사용자한테 랜덤 심박수 데이터 발행
        for (User user : users) {
            // 심박수 랜덤 생성 (60 ~ 160)
            int randomHeartRate = ThreadLocalRandom.current().nextInt(60, 161);

            // 가상 데이터 DTO 생성
            HeartDataRequestDto dto = new HeartDataRequestDto(
                    user.getId(),
                    randomHeartRate,
                    LocalDateTime.now()
            );

            try {
                // DB의 사용자 정보 사용
                heartRateService.record(
                        user.getId(),
                        user.getAge(),
                        user.getGender(),
                        user.getEmail(),
                        dto
                );
            }catch (Exception e) {
                // 사용자의 실패가 전체 루프 멈추지 않게 예외 처리
                log.error("유저 {}의 데이터 생성 실패: {}", user.getId(), e.getMessage());
            }
        }
    }
}
