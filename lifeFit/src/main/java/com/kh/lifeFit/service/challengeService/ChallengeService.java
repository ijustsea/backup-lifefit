package com.kh.lifeFit.service.challengeService;

import com.kh.lifeFit.Exception.AlreadyAppliedException;
import com.kh.lifeFit.domain.challenge.Challenge;
import com.kh.lifeFit.domain.challenge.ChallengeParticipant;
import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.challenge.ChallengeDetailResponse;
import com.kh.lifeFit.dto.challenge.ChallengeListItemResponse;
import com.kh.lifeFit.dto.challenge.ChallengeStatusCountResponse;
import com.kh.lifeFit.dto.challenge.ChallengeSummaryResponse;
import com.kh.lifeFit.repository.challengeRepository.ChallengeParticipantRepository;
import com.kh.lifeFit.repository.challengeRepository.ChallengeRepository;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final UserRepository userRepository;

    public List<ChallengeListItemResponse> getChallengeList() {
        List<ChallengeStatus> statuses = List.of(ChallengeStatus.ONGOING, ChallengeStatus.FULL);

        return challengeRepository.findListByStatusIn(statuses);
    }

    public ChallengeSummaryResponse getChallengeSummary() {
        List<ChallengeStatusCountResponse> summary = challengeRepository.findSummaryByStatus();

        Map<ChallengeStatus, Long> map =
                summary.stream() // 리스트의 객체들에 하나하나 접근해서
                        .collect(Collectors.toMap( // 맵으로 만들어 주는데
                                ChallengeStatusCountResponse::getStatus, // status는 맵의 키로,
                                ChallengeStatusCountResponse::getCount // 카운트는 맵의 value로 설정!
                        ));

        long ongoingCount = map.getOrDefault(ChallengeStatus.ONGOING, 0L);
        long fullCount = map.getOrDefault(ChallengeStatus.FULL, 0L);
        long totalCount = ongoingCount + fullCount;

        return new ChallengeSummaryResponse(ongoingCount, fullCount, totalCount);
    }

    public Optional<ChallengeDetailResponse> getChallengeDetail(Long id) {
        return challengeRepository.findDetailById(id);
    }

    @Transactional
    public void joinChallenge (Long userId, Long challengeId){
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new IllegalStateException("챌린지 없음"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("유저 정보가 없습니다."));
        LocalDateTime appliedAt = LocalDateTime.now();

        if(hasApplied(userId, challengeId)){
            throw new AlreadyAppliedException("이미 참여한 챌린지입니다.");
        }

        challenge.validateJoinable(appliedAt);

        ChallengeParticipant participant = ChallengeParticipant.create(user, challenge);

        try {
            challengeParticipantRepository.save(participant);
        } catch (DataIntegrityViolationException e) {
            log.error("챌린지 참여자 등록 중 에러 발생", e);
            throw new AlreadyAppliedException("이미 참여한 챌린지입니다.");
        }

        int result = challengeRepository.incrementCountAndSetFullIfNeeded(challengeId);
        if(result == 0){
            throw new IllegalStateException("정원 마감된 챌린지입니다.");
        }

    }

    public boolean hasApplied (Long userId, Long challengeId) {
        return challengeParticipantRepository.existsByChallenge_IdAndUser_Id(challengeId, userId);
    }

}
