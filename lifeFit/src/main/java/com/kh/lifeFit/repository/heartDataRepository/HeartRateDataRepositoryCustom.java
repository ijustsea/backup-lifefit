package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataStatsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HeartRateDataRepositoryCustom {

    HeartDataStatsDto findStatsByHeartRate(Long userId);

    List<HeartRateData> findRecentData(Long userId, Pageable pageable);
}
