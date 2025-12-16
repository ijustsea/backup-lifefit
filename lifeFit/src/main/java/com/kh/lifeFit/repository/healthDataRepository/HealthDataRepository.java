package com.kh.lifeFit.repository.healthDataRepository;

import com.kh.lifeFit.domain.healthData.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
}
