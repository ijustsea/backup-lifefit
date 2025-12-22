package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogDashboardDto;
import com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogProcessingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HeartRateLogRepository extends JpaRepository<HeartRateLog, Long> {

    // 상단 시스템 성능 모니터링 통계 (TPS제외 TPS는 서비스에서 계산할 예정)
    @Query("select new com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogDashboardDto(" +
            "sum(case when l.processStatus = 'SUCCESS' then 1L else 0L end), " +  // 1. successCount
            "sum(case when l.processStatus != 'SUCCESS' then 1L else 0L end), " + // 2. failCount
            "cast(coalesce(avg(l.processingTimeMs), 0.0) as double), " +          // 3. avgMs
            "cast(0.0 as double)) " +                                            // 4. tps (0.0 대신 캐스팅 적용)
            "from HeartRateLog l")
    HeartLogDashboardDto getOverallStats();

    // 상단 파티션별 처리 현황 (메서드명을 서비스에서 사용하는 getPartitionStats로 통일)
    @Query("select new com.kh.lifeFit.dto.heartData.adminLogPage.HeartLogProcessingDto(" +
            "l.partitionNo, " +
            "sum(case when l.processStatus = 'SUCCESS' then 1L else 0L end), " +
            "sum(case when l.processStatus != 'SUCCESS' then 1L else 0L end)) " +
            "from HeartRateLog l " +
            "group by l.partitionNo " +
            "order by l.partitionNo asc")
    List<HeartLogProcessingDto> getPartitionStats();

    // 하단 로그 테이블 (기존 JPQL 방식 - Querydsl 도입 전 임시 사용 가능)
    @Query("select l from HeartRateLog l " +
            "where (:userId is null or l.userId = :userId) " +
            "and (:status is null or l.processStatus = :status) " +
            "and (l.createdAt between :start and :end)")
    Page<HeartRateLog> findLogWithFilter(
            @Param("userId") Long userId,
            @Param("status") ProcessStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    // 4. TPS 계산용
    long countByCreatedAtAfter(LocalDateTime localDateTime);
}
