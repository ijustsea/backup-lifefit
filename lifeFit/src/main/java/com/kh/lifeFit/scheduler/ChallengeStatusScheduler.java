package com.kh.lifeFit.scheduler;

import com.kh.lifeFit.domain.challenge.Challenge;
import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import com.kh.lifeFit.repository.challengeRepository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeStatusScheduler {
    private final ChallengeRepository repository;

    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void endExpiredChallenges() {
        LocalDate today = LocalDate.now();

        List<Challenge> expiredChallenges =
                repository.findAllByStatusNotAndEndDateBefore(ChallengeStatus.ENDED, today);

        for (Challenge challenge : expiredChallenges) {
            challenge.endIfExpired(today);
        }
    }
}
