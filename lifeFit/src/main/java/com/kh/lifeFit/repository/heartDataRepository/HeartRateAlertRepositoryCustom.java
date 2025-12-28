package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertListDto;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertSearchRequest;
import com.kh.lifeFit.dto.heartData.alertPage.HeartAlertStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HeartRateAlertRepositoryCustom {

    // 리스트 조회
    Page<HeartAlertListDto> findAlertList(Long userId, HeartAlertSearchRequest request, Pageable pageable);

    // 통계 조회
    HeartAlertStatsDto findAlertStats(Long userId, HeartAlertSearchRequest request);

    // 알림 발생 날짜 조회
    LocalDate getLatestAlertDate(Long userId);
}
