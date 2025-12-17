package com.kh.lifeFit.dto.healthData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealthDataSummaryResponse {

    private long totalUserCount;
    private long highRiskCount;
    private long cautionCount;
    private long normalCount;

}
