package com.kh.lifeFit.dto.heartData.monitoringPage;

import java.util.List;

public record HeartRateDataResponse(
        HeartDataStatsDto heartDataStatsDto,        // 상단 심박수 통계
        List<HeartDataChartDto> heartDataChartDto,  // 상단 심박수 chart.js
        List<HeartDataListDto> heartDataListDto     // 하단 심박수 기록 리스트
){

}
