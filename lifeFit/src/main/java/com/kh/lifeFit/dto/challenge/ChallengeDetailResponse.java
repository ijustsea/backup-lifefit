package com.kh.lifeFit.dto.challenge;

import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChallengeDetailResponse {

    private Long id;
    private String title;
    private String goal;
    private String reward;
    private LocalDate startDate;
    private LocalDate endDate;
    private int participantCount;
    private int participantLimit;
    private ChallengeStatus status;
    private String description;

}
