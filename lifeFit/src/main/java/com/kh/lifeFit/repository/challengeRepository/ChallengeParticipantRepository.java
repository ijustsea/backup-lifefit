package com.kh.lifeFit.repository.challengeRepository;

import com.kh.lifeFit.domain.challenge.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    boolean existsByChallenge_IdAndUser_Id(Long challengeId, Long userId);
}
