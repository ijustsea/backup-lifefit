package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import com.kh.lifeFit.dto.heartData.adminLogPage.*;
import com.kh.lifeFit.repository.heartDataRepository.HeartRateLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeartRateAdminService {

    private final HeartRateLogRepository heartRateLogRepository;

    public HeartLogPageResponse getAdminLogData(HeartLogSearchRequest request, Pageable pageable) {

        // DB에서 기본 통계 조회 (성공/실패 건수, 평균 시간)
        // Repo 쿼리 결과에서 TPS는 아직 0.0인 상태
        HeartLogDashboardDto dbstats = heartRateLogRepository.getOverallStats();

        // TPS 계산 -> 10초간 처리량 기준
        // 로그 개수 조회
        LocalDateTime tenSecondsAgo = LocalDateTime.now().minusSeconds(10);
        long recentCount = heartRateLogRepository.countByCreatedAtAfter(tenSecondsAgo);
        // TPS 계산 시 데이터 0건이라도 0.0으로 계산됨
        double tps = Math.round((recentCount / 10.0) * 100) / 100.0;
        double roundedAvgMs = Math.round(dbstats.avgMs() * 100) / 100.0;

        // TPS 포함된 DTO 생성 (record라서 새로 생성해야함)
        HeartLogDashboardDto finalDashboard = new HeartLogDashboardDto(
                dbstats.successCount(),
                dbstats.failCount(),
                roundedAvgMs,
                tps  // 계산한 TPS 넣기
        );

        // 파티션별 처리 현황 조회
        List<HeartLogProcessingDto> partitionStats = heartRateLogRepository.getPartitionStats();
        // 날짜 범위 로직
        // 시작일 00:00:00 ~ 종료일 23:59:59.999
        LocalDateTime startDateTime = (request.getStartDate() != null)
                ? request.getStartDate().atStartOfDay()
                : LocalDateTime.now().minusDays(7).with(LocalTime.MIN);

        LocalDateTime endDateTime = (request.getEndDate() != null)
                ? request.getEndDate().atTime(LocalTime.MAX)
                : LocalDateTime.now().with(LocalTime.MAX);

        // 로그 테이블 조회 (필터 적용)
        Page<HeartRateLog> logs = heartRateLogRepository.findLogWithFilter(
                request.getUserId(),
                request.getProcessStatus(),
                startDateTime,
                endDateTime,
                pageable
        );

        // entity -> dto 리스트
        List<HeartLogListDto> listDtos = logs.getContent().stream()
                .map(log -> new HeartLogListDto(
                        log.getCreatedAt(),
                        log.getUserId(),
                        log.getPartitionNo(),
                        log.getProcessStatus(),
                        log.getProcessingTimeMs(),
                        log.getRemarks()
                )).toList();

        return new HeartLogPageResponse(finalDashboard, partitionStats, listDtos);
    }
}
