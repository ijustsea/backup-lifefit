package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogDashboardDto;
import com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogProcessingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface HeartRateLogRepositoryCustom {

    // 필터링 조회
    Page<HeartRateLog> findLogsWithFilter(
            Long userId,
            ProcessStatus status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // 상단 시스템 성능 모니터링 통계
    HeartLogDashboardDto getOverallStats();

    // 상단 파티션별 처리 현황
    List<HeartLogProcessingDto> getPartitionStats();

    // 폴링 데이터 조회
    List<HeartRateLog> findPollingData(Long lastId);
}
