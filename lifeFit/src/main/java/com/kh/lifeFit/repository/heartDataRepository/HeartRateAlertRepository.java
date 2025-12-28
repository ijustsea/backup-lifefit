package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRateAlertRepository extends JpaRepository<HeartRateAlert, Long>, HeartRateAlertRepositoryCustom {
}
