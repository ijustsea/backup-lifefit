package com.kh.lifeFit.service.challengeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeFacade {

    private final RedissonClient rc;
    private final ChallengeService service;

    public void joinChallenge(Long userId, Long challengeId) {
        // 락 이름
        String key = "lock:challenge:" + challengeId;

        // 락 객체 가져오기
        RLock lock = rc.getLock(key);

        try {
            long waitTime = 1000;
            boolean isJoinable = lock.tryLock(waitTime, -1, TimeUnit.MILLISECONDS);

            if (!isJoinable) {
                log.info("신청 인원이 많아 락을 획득하지 못했습니다.");
                throw new IllegalStateException("요청이 몰려 잠시 후 다시 시도해주세요.");
            }

            service.joinChallenge(userId, challengeId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
