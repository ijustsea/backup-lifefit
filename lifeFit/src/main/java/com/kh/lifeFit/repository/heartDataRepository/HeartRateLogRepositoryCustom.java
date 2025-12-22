package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import com.kh.lifeFit.domain.heartData.ProcessStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface HeartRateLogRepositoryCustom {

    Page<HeartRateLog> findLogsWithFilter(
            Long userId,
            ProcessStatus status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
