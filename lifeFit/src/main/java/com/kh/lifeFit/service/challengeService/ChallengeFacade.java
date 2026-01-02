package com.kh.lifeFit.service.challengeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChallengeFacade {

    private final RedissonClient rc;
    private final ChallengeService service;

    public ChallengeFacade(ObjectProvider<RedissonClient> rcProvider, ChallengeService service) {
        this.rc = rcProvider.getIfAvailable(); // 빈이 없으면 null이 들어감
        this.service = service;
    }

    @Value("${challenge.lock.wait-time:3000}")
    private long waitTime;

    @Value("${challenge.lock.lease-time:-1}")
    private long leaseTime;

    public void joinChallenge(Long userId, Long challengeId) {
        // 레디스 없는 환경일 경우 락 없이 바로 실행하고 종료
        if (rc == null) {
            log.info("Redis가 비활성화되어 락 없이 챌린지 참가를 진행합니다.");
            service.joinChallenge(userId, challengeId);
            return;
        }

        // 락 이름
        String key = "lock:challenge:" + challengeId;

        // 락 객체 가져오기
        RLock lock = rc.getLock(key);

        try {
            boolean isJoinable = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);

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
