package com.kh.lifeFit.dto.heartData.adminLogPage;

import org.springframework.data.domain.Page;

import java.util.List;

public record HeartLogPageResponse(
        HeartLogDashboardDto heartLogDashboardDto,              // 상단 : 시스템 성능 모니터링(종합 통계)
        List<HeartLogProcessingDto> heartLogProcessingDtos,     // 상단 : 파티션별 처리 현황(리스트)
        Page<HeartLogListDto> heartLogListDtos                  // 하단 : 로그 테이블(리스트)
) {
}
