package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.common.Gender;
import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertListDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertStatsDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartRateAlertResponse;
import com.kh.lifeFit.dto.heartData.monitoringPage.*;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateAlertRepository;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateLogRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kh.lifeFit.domain.heartData.QHeartRateData.heartRateData;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 성능 향상
public class HeartRateService {

    private final Optional<HeartRateProducer> heartRateProducer;
    private final HeartRateDataRepository heartRateDataRepository;
    private final HeartRateAlertRepository heartRateAlertRepository; // 알림
    private final JPAQueryFactory queryFactory;

    /**
     * 심박수 데이터 기록 및 관리자 로그 생성
     */
    @Transactional
    public void record(Long userId, int age, Gender gender, String email, HeartDataRequestDto dto){
        long startTime = System.currentTimeMillis(); // 처리 시작 시간 측정

        // 인증된 userId를 DTO에 강제로 주입 (보안 핵심으로 IDOR 방지)
        // record는 불변객체이기 때문에 메서드 호출 방식으로 한다.
        HeartDataRequestDto securedDto = new HeartDataRequestDto(
                userId,
                dto.heartRate(),
                dto.measuredAt()
        );

        // Producer에게 위임하기
        // DB 저장 | kafka 방식이든 동일
        heartRateProducer.ifPresent(heartRateProducer -> heartRateProducer.send(securedDto, age, gender, email));
    }

    /**
     *  실시간 심박수 페이지 데이터 조회
     */
    // 실시간 심박수 데이터(현재 심박수 | 평균 심박수 | 최고 심박수)
    public HeartRateDataResponse getDashboardData(Long userId, int age, Gender gender, int limit) {

        //============ 오늘의 심박수 리스트 조회(1분 단위 집계 & 원본 데이터) ============
        // 0페이지부터 limit 개수만큼 가져오라는 뜻 (Top N 쿼리와 비슷)
        Pageable pageable = PageRequest.of(0, limit);
        // 심박수 리스트 최근 데이터 10개 가져오기 (DB 조회)
        List<HeartDataListDto> recentDatalist = heartRateDataRepository.findRecentDataList(userId, pageable);

        // 변화량 실시간 계싼 로직 적용
        List<HeartDataListDto> processedList = new ArrayList<>();
        for (int i = 0; i < recentDatalist.size(); i++) {
            HeartDataListDto currentData = recentDatalist.get(i);
            int calculatedVariation = 0;

            // 최신순 정렬 i번째와 i+1번째(직전 시간) 비교
            if (i < recentDatalist.size() - 1) {
                HeartDataListDto previousData = recentDatalist.get(i + 1);
                calculatedVariation = currentData.heartRate() - previousData.heartRate();
            }

            // 평균 심박수로 상태 재계산
            HeartRateStatus recalculatedStatus = HeartRateStatus.getHeartRateStatus(
                    currentData.heartRate(), // 평균 심박수
                    age,                     // JWT에서 받은 사용자의 나이
                    gender                   // JWT에서 받은 사용자의 성별
            );

            // Record는 불변, 새로 생성하여 리스트에 추가하기
            processedList.add(new HeartDataListDto(
                    currentData.id(),
                    currentData.measuredAt(),
                    currentData.heartRate(),
                    calculatedVariation, // 계산된 변화량
                    HeartDataListDto.calculateTimeAgo(currentData.measuredAt()), // 실시간 계산된 경과 시간
                    recalculatedStatus   // DB의 max(status) 대신 사용하기
            ));
        }

        // 현재 상태(Status) 추출
        // 리스트가 비어있을 수 있으니 안전하게 처리
        HeartRateStatus currentStatus = HeartRateStatus.NORMAL; // 기본값
        if(!processedList.isEmpty()){
            // 최신 데이터(0번)의 상태 가져오기
            currentStatus = processedList.get(0).status();
        }

        //============ 심박수 통계 조회 ============
        // 심박수 통계(현재 심박수, 평균 심박수, 최고 심박수)
        HeartDataStatsDto statsDto = heartRateDataRepository.findStatsByHeartRate(userId);
        // DB에서 가져온 통계 + 방금 구한 Status 합치기
        HeartDataStatsDto findStatsDto = new HeartDataStatsDto(
                statsDto.currentHeartRate(),
                statsDto.avgHeartRate(),
                statsDto.maxHeartRate(),
                currentStatus
        );

        //============ 최근 30분간 심박수 추이 차트 조회 ============
        // 기준 시간 설정(현재 시간 기준으로 30분 전 시간)
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        // 차트 데이터 가져오기
        List<HeartDataChartDto> chartData = heartRateDataRepository.findChartData(userId, thirtyMinutesAgo);

        // 반환 (HeartDataChartDto)
        return new HeartRateDataResponse(findStatsDto, chartData, processedList);
    }


    /**
     * 심박수 알림 내역 페이지 데이터 조회
     */
    public HeartRateAlertResponse getAlertData(Long userId, HeartAlertSearchRequest request, Pageable pageable) {
        // 기준 날짜 가져오기 (데이터가 없으면 오늘, 있으면 가장 최근 데이터 날짜)
        LocalDate lastestDate = heartRateAlertRepository.getLatestAlertDate(userId);
        // [SSOT] 실제 쿼리에 사용할 시작일과 종료일 확정 (입력된 날짜 없으면 최근 데이터 날짜)
        LocalDate findStartDate = (request.getStartDate() != null) ? request.getStartDate() : lastestDate;
        LocalDate findEndDate = (request.getEndDate() != null) ? request.getEndDate() : lastestDate;
        // 확정된 날짜를 파라미터로 직접 전달 -> 상단 통계 & 하단 리스트 조회
        HeartAlertStatsDto stats = heartRateAlertRepository.findAlertStats(userId, request, findStartDate, findEndDate);
        Page<HeartAlertListDto> list = heartRateAlertRepository.findAlertList(userId, request, pageable, findStartDate, findEndDate);

        // SSOT 기반의 모든 데이터를 Response에 담아 반환
        return new HeartRateAlertResponse(stats, list, findStartDate, findEndDate);
    }

    public List<HeartDataListDto> getRecentDataList(Long userId, Long lastId, int age, Gender gender) {

        Pageable pageable = PageRequest.of(0, 3);
        List<HeartDataListDto> aggregatedList = heartRateDataRepository.findRecentDataList(userId, pageable);

        List<HeartDataListDto> resultList = new ArrayList<>();

        for (int i = 0; i < aggregatedList.size(); i++) {
            HeartDataListDto current = aggregatedList.get(i);
            int variation = 0;
            // 직전 분(i+1)의 평균 데이터가 있다면 차이 계산
            if (i < aggregatedList.size() - 1) {
                HeartDataListDto previous = aggregatedList.get(i + 1);
                variation = current.heartRate() - previous.heartRate();
            }

            // 실시간 상태 재계산 (현재 분의 평균 심박수 기준)
            HeartRateStatus status = HeartRateStatus.getHeartRateStatus(current.heartRate(), age, gender);
            resultList.add(new HeartDataListDto(
                    current.id(),
                    current.measuredAt(),
                    current.heartRate(),
                    variation, // 1분 평균 간의 변화량
                    HeartDataListDto.calculateTimeAgo(current.measuredAt()),
                    status
            ));
        }
        return resultList;
    }
}
