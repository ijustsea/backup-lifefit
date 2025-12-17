package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbHeartRateProducer implements HeartRateProducer {

    private final UserRepository userRepository;                    // 회원정보 조회(성별, 나이)
    private final HeartRateDataRepository heartRateDataRepository;  // DB 저장소


    @Override
    public void send(HeartDataRequestDto dto) {
        // 1. 사용자(직원) 정보 조회
        Long userId = dto.userId();
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 나이 조회
        int userAge = user.getAge();

        // 3. 엔티티 생성 (엔티티 내부에서 스스로 위험/정상 판단을 끝냄)
        HeartRateData heartRateData = HeartRateData.builder()
                .userId(userId)
                .heartRate(dto.heartRate())
                .measuredAt(dto.measuredAt())
                .variation(0)
                .age(userAge)
                .gender(user.getGender())
                .build();

        heartRateDataRepository.save(heartRateData);
    }
}
