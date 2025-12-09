package com.kh.lifeFit.domain.challenge;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
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

}
