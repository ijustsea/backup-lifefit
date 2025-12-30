package com.kh.lifeFit.service.challengeService;

import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import com.kh.lifeFit.dto.challenge.ChallengeDetailResponse;
import com.kh.lifeFit.dto.challenge.ChallengeListItemResponse;
import com.kh.lifeFit.dto.challenge.ChallengeStatusCountResponse;
import com.kh.lifeFit.dto.challenge.ChallengeSummaryResponse;
import com.kh.lifeFit.repository.challengeRepository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repository;

    public List<ChallengeListItemResponse> getChallengeList() {
        List<ChallengeStatus> statuses = List.of(ChallengeStatus.ONGOING, ChallengeStatus.FULL);

        return repository.findListByStatusIn(statuses);
    }

    public ChallengeSummaryResponse getChallengeSummary() {
        List<ChallengeStatusCountResponse> summary = repository.findSummaryByStatus();

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
        return repository.findDetailById(id);
    }

}
