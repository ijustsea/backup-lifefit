package com.kh.lifeFit.service.healthDataService;

import com.kh.lifeFit.dto.healthData.HealthDataFilterRequest;
import com.kh.lifeFit.dto.healthData.HealthDataResponse;
import com.kh.lifeFit.repository.healthDataRepository.HealthDataQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HealthDataService {

    private final HealthDataQueryRepository repository;

    public Page<HealthDataResponse> getHealthDataList(HealthDataFilterRequest filter, Pageable pageable) {
        return repository.selectHealthDataList(filter, pageable);
    }

}
