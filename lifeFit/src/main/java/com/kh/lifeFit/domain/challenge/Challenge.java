package com.kh.lifeFit.domain.challenge;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Challenge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private String reward;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private int participantCount = 0;

    @Column(nullable = false)
    private int participantLimit;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status; // ONGOING, FULL, ENDED // 진행중, 선착순 마감, 종료

    @Column(length = 1000)
    private String description;

    public boolean isJoinable (LocalDateTime appliedDate) {
        LocalDate applied = appliedDate.toLocalDate();
        return participantLimit > participantCount && status == ChallengeStatus.ONGOING && !applied.isBefore(startDate) && !applied.isAfter(endDate);
    }

    public void validateJoinable (LocalDateTime appliedDate) {
        if (!isJoinable(appliedDate)) {
            throw new IllegalStateException("참여할 수 없는 챌린지입니다.");
        }
    }

    public void increaseParticipantCount () {
        participantCount++;
        if (participantCount == participantLimit) {
            status = ChallengeStatus.FULL;
        }
    }

    public void endIfExpired (LocalDate today) {
        if (status != ChallengeStatus.ENDED && endDate.isBefore(today)){
            status = ChallengeStatus.ENDED;
        }
    }

}
