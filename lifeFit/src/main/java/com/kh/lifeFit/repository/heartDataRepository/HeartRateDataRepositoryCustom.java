package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataChartDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface HeartRateDataRepositoryCustom {

    HeartDataStatsDto findStatsByHeartRate(Long userId);

    // 상단 '최근 30분간 심박수 추이'
    List<HeartDataChartDto> findChartData(Long userId, LocalDateTime startTime);

    List<HeartRateData> findRecentData(Long userId, Pageable pageable);
}
