package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.heartData.HeartRateAlert;
import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataListDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;
import com.kh.lifeFit.monitor.SystemMonitor;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateAlertRepository;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbHeartRateProducer implements HeartRateProducer {

    private final HeartRateDataRepository heartRateDataRepository;  // DB 저장소
    private final HeartRateAlertRepository hearRateAlertRepository; // 알림 내역
    private final SystemMonitor systemMonitor;                      // 시스템 로그 모니터링


    @Override
    public void send(HeartDataRequestDto dto, int age, Gender gender, String email) {
        long startTime = System.currentTimeMillis();
        int virtualPartition = (int) (dto.userId() % 4); // 가상 파티션 번호 0,1,2,3

        try {
            // 변화량 계산용 -> 최신 심박수 데이터 1건 조회
            // PageRequst.of(0,1)을 사용
            int lastHeartRate = heartRateDataRepository.findRecentDataList(dto.userId(), PageRequest.of(0,1))
                    .stream()
                    .map(HeartDataListDto::heartRate)
                    .findFirst()
                    .orElse(dto.heartRate()); // 첫 측정은 데이터 X 변화량 0으로 처리하기

            // 엔티티 생성 (엔티티 내부에서 스스로 위험/정상 판단을 끝냄)
            HeartRateData heartRateData = HeartRateData.builder()
                    .userId(dto.userId())
                    .heartRate(dto.heartRate())
                    .measuredAt(dto.measuredAt())
                    .variation(dto.heartRate() - lastHeartRate)
                    .age(age)        // 파라미터 사용
                    .gender(gender)  // 파라미터 사용
                    .build();

            // DB 저장
            heartRateDataRepository.save(heartRateData);

            // 비정상 상태인 경우 알림 테이블에 저장
            if (heartRateData.getStatus().isAbnormal()){
                HeartRateAlert alert = new HeartRateAlert(heartRateData);
                hearRateAlertRepository.save(alert);
            }

            // 성공 로그 기록
            long duration = System.currentTimeMillis() - startTime;
            systemMonitor.recordHeartRateLog(dto.userId(), email, virtualPartition, (int) duration, ProcessStatus.SUCCESS, null);
        } catch (IllegalArgumentException e) {
            // 검증 실패 로그
            systemMonitor.recordHeartRateLog(dto.userId(), email, virtualPartition, 0, ProcessStatus.FAIL_VALIDATION, e.getMessage());
            throw e;
        } catch (Exception e){
            // 서버 내부 오류 로그
            systemMonitor.recordHeartRateLog( dto.userId(), email, virtualPartition, 0, ProcessStatus.FAIL_SERVER, e.getMessage());
            throw e;
        }
    }
}
