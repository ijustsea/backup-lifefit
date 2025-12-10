package com.kh.lifeFit.repository.HeartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRateDataRepository extends JpaRepository<HeartRateData, Long> {

}
