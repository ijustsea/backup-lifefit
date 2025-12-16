package com.kh.lifeFit.dto.heartData.alertPage;

import java.util.List;

public record HeartRateAlertResponse(
        HeartAlertStatsDto stats,       // 상단 알림 통계
        List<HeartAlertListDto> list    // 하단 알림 상세 내역
) {

}
