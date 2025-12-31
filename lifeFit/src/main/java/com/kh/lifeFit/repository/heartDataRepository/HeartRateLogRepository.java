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

public interface HeartRateLogRepository extends JpaRepository<HeartRateLog, Long>, HeartRateLogRepositoryCustom {
    // TPS 계산용(단순 카운트라 JpaRepository 활용)
    long countByCreatedAtAfter(LocalDateTime localDateTime);
}
