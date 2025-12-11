package com.kh.lifeFit.dto.heartData.MonitoringPage;

public record HeartRateDataResponse(
        HeartDataDashboardDto heartDataDashboardDto, // 상단 심박수 통계
        HeartDataChartDto heartDataChartDto,         // 상단 심박수 chart.js
        HeartDataListDto heartDataListDto            // 하단 심박수 기록 리스트
){

}
