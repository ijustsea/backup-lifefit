package com.kh.lifeFit.repository.challengeRepository;

import com.kh.lifeFit.domain.challenge.Challenge;
import com.kh.lifeFit.domain.challenge.ChallengeStatus;
import com.kh.lifeFit.dto.challenge.ChallengeDetailResponse;
import com.kh.lifeFit.dto.challenge.ChallengeListItemResponse;
import com.kh.lifeFit.dto.challenge.ChallengeStatusCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Challenge c
            set c.status = case
                when (c.participantCount + 1) >= c.participantLimit
                    then com.kh.lifeFit.domain.challenge.ChallengeStatus.FULL
                        else c.status end,
                            c.participantCount = (c.participantCount + 1)
                                where c.id = :id
                                    and c.status = com.kh.lifeFit.domain.challenge.ChallengeStatus.ONGOING
                                        and c.participantCount < c.participantLimit
    """)
    int incrementCountAndSetFullIfNeeded (@Param("id") Long challengeId);

}
