package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.monitor.SystemMonitor;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbHeartRateProducer implements HeartRateProducer {

    private final UserRepository userRepository;                    // 회원정보 조회(성별, 나이)
    private final HeartRateDataRepository heartRateDataRepository;  // DB 저장소
    private final SystemMonitor systemMonitor;                      // 시스템 로그 모니터링


    @Override
    public void send(HeartDataRequestDto dto) {
        long startTime = System.currentTimeMillis();
        int virtualPartition = (int) (dto.userId() % 3); // 가상 파티션 번호 0,1,2
        String username = "Unknown";                     // 실패 시 기본값

        try {
            // 1. 사용자(직원) 정보 조회
            User user = userRepository.findById(dto.userId())
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

            username = user.getName();

            // 2. 변화량 계산용 -> 최신 심박수 데이터 1건 조회
            // PageRequst.of(0,1)을 사용
            int lastHeartRate = heartRateDataRepository.findRecentData(dto.userId(), PageRequest.of(0,1))
                    .stream()
                    .map(HeartRateData::getHeartRate)
                    .findFirst()
                    .orElse(dto.heartRate()); // 첫 측정은 데이터 X 변화량 0으로 처리하기

            // 3. 엔티티 생성 (엔티티 내부에서 스스로 위험/정상 판단을 끝냄)
            HeartRateData heartRateData = HeartRateData.builder()
                    .userId(dto.userId())
                    .heartRate(dto.heartRate())
                    .measuredAt(dto.measuredAt())
                    .variation(dto.heartRate() - lastHeartRate)
                    .age(user.getAge())
                    .gender(user.getGender())
                    .build();

            // DB 저장
            heartRateDataRepository.save(heartRateData);

            // 성공 로그 기록
            long duration = System.currentTimeMillis() - startTime;
            systemMonitor.recordHeartRateLog(dto.userId(), username, virtualPartition, (int) duration, ProcessStatus.SUCCESS, null);
        } catch (IllegalArgumentException e) {
            // 검증 실패 로그
            systemMonitor.recordHeartRateLog(dto.userId(), username, virtualPartition, 0, ProcessStatus.FAIL_VALIDATION, e.getMessage());
            throw e;
        } catch (Exception e){
            // 서버 내부 오류 로그
            systemMonitor.recordHeartRateLog( dto.userId(), username, virtualPartition, 0, ProcessStatus.FAIL_SERVER, e.getMessage());
            throw e;
        }
    }
}
