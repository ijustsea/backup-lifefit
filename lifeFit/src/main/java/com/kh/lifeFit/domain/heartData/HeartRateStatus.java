package com.kh.lifeFit.domain.heartData;

import com.kh.lifeFit.domain.common.Gender;
import lombok.Getter;

@Getter
public enum HeartRateStatus { // 상태 정의만 담당한다.

    NORMAL("정상", "#28a745"),
    CAUTION("경고", "#ffc107"),
    DANGER("위험", "#dc3545");

    private final String displayName;
    private final String colorCode;

    // 생성자
    HeartRateStatus(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public boolean isAbnormal(){
        return this != NORMAL;
    }

    // 성별&나이 기반 심박수 상태 판단 메소드
    public static HeartRateStatus getHeartRateStatus(int heartRate, int age, Gender gender) {

        // enum 필드X, 상태X, 규칙 계산용 값
        int minNormal; // 싱박수 정상 범위 최소값
        int maxNormal; // 심박수 정상 범위 최대값

        // if~else구문에 조건판단1회로 줄이기 위해 리팩토링함(가독성 업)
        boolean isMale = gender == Gender.MALE;

        if (age < 30) {
            minNormal = isMale ? 60 : 65;
            maxNormal = isMale ? 100 : 105;
        } else if (age >= 30 && age < 50) {
            minNormal = isMale ? 58 : 63;
            maxNormal = isMale ? 98 : 103;
        } else if (age >= 50 && age < 65) {
            minNormal = isMale ? 55 : 60;
            maxNormal = isMale ? 95 : 100;
        } else {
            minNormal = isMale ? 50 : 55;
            maxNormal = isMale ? 90 : 95;
        }

        // 상태 판단
        if (heartRate >= minNormal && heartRate <= maxNormal) {
            return NORMAL;
        } else if ((heartRate >= minNormal - 10 && heartRate < minNormal) ||
                (heartRate > maxNormal && heartRate <= maxNormal + 20)) {
            return CAUTION;
        } else {
            return DANGER;
        }
    }

}
