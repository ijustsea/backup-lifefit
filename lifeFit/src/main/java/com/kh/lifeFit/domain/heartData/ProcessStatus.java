package com.kh.lifeFit.domain.heartData;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessStatus { // HeartRateStatus와 다르게 운영 규칙을 설정한다.

    SUCCESS("성공"),

    // 실패 상태 세분화(비고란에 자동 입력용)
    FAIL_TIMEOUT("외부 시스템 시간 초과"),// 시스템 문제 (DB, Kafka, API)
    FAIL_VALIDATION("데이터 검증 실패"), // 데이터 문제 (값 이상)
    FAIL_SERVER("서버 내부 오류"),       // 버그, NPE, 설정
    FAIL_UNKNOWN("알 수 없음");         // 기타 에러

    // 실패 시 로그/비고란에 사용되는 설명
    private final String description;

    // 성공 건수 카운팅용
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    // 실패 건수 카운팅용
    public boolean isFail() {
        return this != SUCCESS; // 실패 세분화로 이걸로 통계 사용한다.
    }

    /**
     * 로그 테이블 '비고'란 내용 생성 메서드
     *
     * @param detailMessage 구체적인 에러 메시지 (없으면 null)
     * @return 성공이면 "-", 실패면 에러 메시지 또는 Enum 설명
     */
    public String resolveRemarks(String detailMessage) {

        // 구체적인 에러 메시지O, 그대로 반환
        if(detailMessage != null && !detailMessage.isBlank()) {
            return detailMessage;
        }

        // 메시지X, 성공 "-" 하이픈 사용
        if(this == SUCCESS) {
            return "-";
        }

        // 메시지X, 실패 세분화 사용
        return this.description;

    }


}
