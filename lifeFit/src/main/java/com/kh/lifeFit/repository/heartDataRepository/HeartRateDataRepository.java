package com.kh.lifeFit.repository.heartDataRepository;

import com.kh.lifeFit.domain.heartData.HeartRateData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeartRateDataRepository
        extends JpaRepository<HeartRateData, Long>, HeartRateDataRepositoryCustom{

}
