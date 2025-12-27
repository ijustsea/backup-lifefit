package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertListDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertStatsDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataChartDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataListDto;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HeartRateDataRepositoryCustom {

    HeartDataStatsDto findStatsByHeartRate(Long userId);

    // 상단 '최근 30분간 심박수 추이'
    List<HeartDataChartDto> findChartData(Long userId, LocalDateTime startTime);

    List<HeartDataListDto> findRecentDataList(Long userId, Pageable pageable);

}
