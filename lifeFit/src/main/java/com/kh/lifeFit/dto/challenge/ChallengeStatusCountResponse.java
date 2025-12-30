package com.kh.lifeFit.dto.challenge;

import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeStatusCountResponse {
    private ChallengeStatus status;
    private long count;
}
