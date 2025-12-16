package com.kh.lifeFit.dto.heartData.adminLogPage;

import com.kh.lifeFit.domain.heartData.ProcessStatus;

import java.time.LocalDateTime;

public record HeartLogListDto(
        LocalDateTime processingTime,   // 처리 시간
        Long userId,                    // 직원ID
        Integer partitionNo,            // 파티션 번호
        ProcessStatus processStatus,    // 처리 상태 "성공" "실패"
        int processingTimeMs,           // 소요시간(ms)
        String remarks                  // 비고
) {
}
