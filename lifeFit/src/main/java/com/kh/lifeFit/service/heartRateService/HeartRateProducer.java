package com.kh.lifeFit.service.heartRateService;

import com.kh.lifeFit.dto.heartData.MonitoringPage.HeartDataRequestDto;

public interface HeartRateProducer {

    void send(HeartDataRequestDto dto);

}
