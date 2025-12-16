package com.kh.lifeFit.dto.heartData.adminLogPage;

public record HeartLogDashboardDto(
        Long totalCount,    // 총 건
        Long successCount,  // 성공 건수
        Long failCount,     // 실패 건수
        Double avgMs,       // 평균 소요 시간
        Double tps
) {

    public HeartLogDashboardDto(Long successCount, Long failCount, Double avgMs, Double tps) {
        this(successCount + failCount, successCount, failCount, avgMs, tps);
    }
}
