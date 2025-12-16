package com.kh.lifeFit.dto.heartData.adminLogPage;

public record HeartLogProcessingDto(
        int partitionNo,   // 파티션 번호
        Long successCount, // 성공 건수
        Long failCount     // 실패 건수
) {
}
