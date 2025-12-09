package com.kh.lifeFit.domain.heartData;

import lombok.Getter;

@Getter
public enum HeartRateStatus {

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

    //
    public static HeartRateStatus getHeartRateStatus(int heartRate, int age, String gender) {

        int minNormal;
        int maxNormal;

        if (age < 30) {
            minNormal = gender.equals("male") ? 60 : 65;
            maxNormal = gender.equals("male") ? 100 : 105;
        } else if (age >= 30 && age < 50) {
            minNormal = gender.equals("male") ? 58 : 63;
            maxNormal = gender.equals("male") ? 98 : 103;
        } else if (age >= 50 && age < 65) {
            minNormal = gender.equals("male") ? 55 : 60;
            maxNormal = gender.equals("male") ? 95 : 100;
        } else {
            minNormal = gender.equals("male") ? 50 : 55;
            maxNormal = gender.equals("male") ? 90 : 95;
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
