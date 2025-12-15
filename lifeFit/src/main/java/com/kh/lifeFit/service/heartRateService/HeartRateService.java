package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.dto.heartData.MonitoringPage.HeartDataRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartRateService {

    private final HeartRateProducer heartRateProducer;

    public void record(HeartDataRequestDto dto){
        heartRateProducer.send(dto);
    }


}
