package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.dto.heartData.monitoringPage.*;

import com.kh.lifeFit.repository.heartDataRepository.HeartRateDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    // 실시간 심박수 데이터 페이지 서비스 계층
    public HeartRateDataResponse getDashboardData(Long userId, int limit) {

        //============ 리스트 조회 ============
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

        // ============ 통계 조회 ============
        // 심박수 통계(현재 심박수, 평균 심박수, 최고 심박수)
        HeartDataStatsDto statsDto = heartRateDataRepository.findStatsByHeartRate(userId);

        // 반환 (HeartDataChartDto)
        return new HeartRateDataResponse(statsDto,null, listItems);

    }
}
