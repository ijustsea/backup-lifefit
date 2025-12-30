package com.kh.lifeFit.repository.challengeRepository;

import com.kh.lifeFit.domain.challenge.Challenge;
import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import com.kh.lifeFit.dto.challenge.ChallengeDetailResponse;
import com.kh.lifeFit.dto.challenge.ChallengeListItemResponse;
import com.kh.lifeFit.dto.challenge.ChallengeStatusCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query(
        "select new com.kh.lifeFit.dto.challenge.ChallengeListItemResponse(c.id, c.title, c.startDate, c.endDate, c.participantCount, c.participantLimit, c.status) " +
                " from Challenge c" +
                " where c.status in (:statuses)" +
                " order by c.startDate desc"
    )
    List<ChallengeListItemResponse> findListByStatusIn(@Param("statuses") List<ChallengeStatus> statuses);

    @Query("""
        select new com.kh.lifeFit.dto.challenge.ChallengeStatusCountResponse(c.status, count(c))
            from Challenge c
                group by c.status
    """)
    List<ChallengeStatusCountResponse> findSummaryByStatus();

    @Query("""
        select new com.kh.lifeFit.dto.challenge.ChallengeDetailResponse(
            c.id, c.title, c.goal, c.reward, c.startDate, c.endDate,
                c.participantCount, c.participantLimit, c.status, c.description
        )
            from Challenge c
                where c.id = :id
    """)
    Optional<ChallengeDetailResponse> findDetailById(@Param("id") long id);

    List<Challenge> findAllByStatusNotAndEndDateBefore(ChallengeStatus status, LocalDate date);


}
