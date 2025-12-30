package com.kh.lifeFit.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeSummaryResponse {

    private long ongoingCount;
    private long fullCount;
    private long totalCount;

}
