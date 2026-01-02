package com.kh.lifeFit.service.challengeService;

import com.kh.lifeFit.domain.challenge.Challenge;
import com.kh.lifeFit.repository.challengeRepository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ChallengeConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(ChallengeConcurrencyTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChallengeFacade facade;

    @Autowired
    private ChallengeRepository challengeRepository;

    private Long challengeId;

    @Test
    void joinChallenge_concurrency() throws InterruptedException {
        // given
        challengeId = 5L;

        int threadCount = 45; // 45명 동시요청

        // ExecutorService: 비동기 작업 도와주는 자바의 스레드 풀 관리자
        // new FixedThreadPool(32): 동시에 32개 스레드가 작업 처리 (일반적인 설정)
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch: 45개의 작업이 끝날 때까지 메인 스레드 대기시킴
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 6; i <= 50; i++) {
            long userId = (long) i;

            executorService.submit(() -> {
                try {
                    facade.joinChallenge(userId, challengeId);
                } catch (Exception e) {
                    // 선착순 마감도 예외! 로그 찍고 넘어감
                    log.error("챌린지 선착순 참여 테스트 중 오류 발생 클래스: {}, 메시지: {}", e.getClass(), e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 45명이 다 끝날 때까지 대기

        // then
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow();

        // 결과로그 찍어보기
        log.info("최종 참여자 수: {} / {}", challenge.getParticipantCount(), challenge.getParticipantLimit());

        // 검증: 챌린지 참여인원이 정원 초과 되지 않았는지 (5번 인덱스 챌린지 정원 10명)
        assertThat(challenge.getParticipantCount()).isEqualTo(10);
        executorService.shutdown();

    }

    @BeforeEach
    void tearDown() {
        if (challengeId == null) return;

        // "내가 어지른 것만 치운다"
        jdbcTemplate.update("DELETE FROM challenge_participant WHERE challenge_id = ?", challengeId);

        // 챌린지 상태도 원복 (다음 테스트를 위해)
        jdbcTemplate.update("UPDATE challenge SET participant_count = 0, status = 'ONGOING' WHERE challenge_id = ?", challengeId);
    }

}
