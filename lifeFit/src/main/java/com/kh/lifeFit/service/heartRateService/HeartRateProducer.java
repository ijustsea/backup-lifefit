package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.dto.heartData.monitoringPage.HeartDataRequestDto;

public interface HeartRateProducer {

    void send(HeartDataRequestDto dto);

}
