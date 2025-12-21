package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertListDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertStatsDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartRateAlertResponse;
import com.kh.lifeFit.dto.heartData.monitoringPage.*;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 성능 향상
public class HeartRateService {

    private final HeartRateProducer heartRateProducer;
    private final HeartRateDataRepository heartRateDataRepository;

    @Transactional
    public void record(HeartDataRequestDto dto){
        heartRateProducer.send(dto);
    }

    /**
     *  실시간 심박수 페이지 데이터 조회
     */
    // 실시간 심박수 데이터(현재 심박수 | 평균 심박수 | 최고 심박수)
    public HeartRateDataResponse getDashboardData(Long userId, int limit) {

        //============ 오늘의 심박수 리스트 조회 ============
        // 0페이지부터 limit 개수만큼 가져오라는 뜻 (Top N 쿼리와 비슷)
        Pageable pageable = PageRequest.of(0, limit);
        // 심박수 리스트 최근 데이터 10개 가져오기 (DB 조회)
        List<HeartRateData> recentDatalist = heartRateDataRepository.findRecentData(userId, pageable);
        // 실시간 심박수 데이터 하단 리스트 DTO
        // 엔티티 -> DTO 변환 (리스트 내부 아이템 변환)
        List<HeartDataListDto> listItems = recentDatalist.stream()
                .map(HeartDataListDto::from)
                .toList();
        // == .map(entity-> HeartDataListDto.from(entity)).toList();

        //============ 현재 상태(Status) 추출 ============
        // 리스트가 비어있을 수 있으니 안전하게 처리
        HeartRateStatus currentStatus = HeartRateStatus.NORMAL; // 기본값
        if(!recentDatalist.isEmpty()){
            // 최신 데이터(0번)의 상태 가져오기
            currentStatus = recentDatalist.get(0).getStatus();
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
        return new HeartRateDataResponse(findStatsDto, chartData, listItems);
    }


    /**
     * 심박수 알림 내역 페이지 데이터 조회
     */
    public HeartRateAlertResponse getAlertData(Long userId, HeartAlertSearchRequest request, Pageable pageable) {

        // 날짜 확정 로직
        // 통계용 -> 첫 진입
        LocalDate latestDate = heartRateDataRepository.getLatestDate(userId);

        // 상단 통계용
        // 오늘 데이터 없으면 DB에서 사용자의 가장 최근 데이터 날짜로 가져오기
        HeartAlertSearchRequest statsRequest = new HeartAlertSearchRequest();
        if (request.getStartDate() != null && request.getEndDate() != null) {
            statsRequest.setStartDate(request.getStartDate());
            statsRequest.setEndDate(request.getEndDate());
        }else {
            statsRequest.setStartDate(latestDate);
            statsRequest.setEndDate(latestDate);
        }
        HeartAlertStatsDto stats = heartRateDataRepository.findAlertStats(userId, statsRequest);

        // 하단 리스트
        // 첫 진입시 request.startDate = null이라서 전체 조회
        Page<HeartAlertListDto> list = heartRateDataRepository.findAlertList(userId, request, pageable);

        LocalDate startDate = (request.getStartDate() != null) ? request.getStartDate() : latestDate;
        LocalDate endDate = (request.getEndDate() != null) ? request.getEndDate() : latestDate;

        // SSOT 기반의 모든 데이터를 Response에 담아 반환
        return new HeartRateAlertResponse(stats, list, startDate, endDate);
    }
}
