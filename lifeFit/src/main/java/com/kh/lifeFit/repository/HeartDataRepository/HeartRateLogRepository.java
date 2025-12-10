package com.kh.lifeFit.repository.HeartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRateLogRepository extends JpaRepository<HeartRateLog, Long> {
}
