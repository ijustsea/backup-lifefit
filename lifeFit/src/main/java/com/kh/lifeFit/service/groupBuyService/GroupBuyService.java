    package com.kh.lifeFit.service.groupBuyService;

    import com.kh.lifeFit.domain.groupBuy.GroupBuy;
    import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
    import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
    import com.kh.lifeFit.domain.user.User;
    import com.kh.lifeFit.repository.groupBuyRepository.GroupBuyInfoRepository;
    import com.kh.lifeFit.repository.groupBuyRepository.GroupBuyRepository;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.OptimisticLockException;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.orm.ObjectOptimisticLockingFailureException;
    import org.springframework.retry.annotation.Backoff;
    import org.springframework.retry.annotation.Retryable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.Optional;
    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class GroupBuyService {
        private final GroupBuyRepository groupBuyRepository;
        private final GroupBuyInfoRepository groupBuyInfoRepository;
        private final EntityManager em;

        @Retryable(
                value = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
                maxAttempts = 5,
                backoff = @Backoff(delay = 50)
        )
        @Transactional
        public GroupBuyStatus participate(Long groupBuyInfoId, Long userId) {

            log.info("🟡 [TRY] userId={} 참여 시도", userId);

            // 1) 공동구매 대상 조회
            GroupBuyInfo info = groupBuyInfoRepository.findByIdForUpdate(groupBuyInfoId)
                    .orElseThrow(() -> new IllegalArgumentException("공동구매 정보를 찾을 수 없습니다."));

            // 2) 공동구매 참여여부 확인
            Optional<GroupBuy> optional = groupBuyRepository.findByUserIdAndGroupBuyInfoId(userId, groupBuyInfoId);

            // 3) 최초참여
            if (optional.isEmpty()){
                info.decreaseLimitStock(); // 재고 검증 + 감소

                User userRef = em.getReference(User.class, userId); // 🔥 프록시
                groupBuyRepository.save(new GroupBuy(userRef, info, GroupBuyStatus.BUY));

                return GroupBuyStatus.BUY;
            }
            // 4) 이미 참여존재
            GroupBuy groupBuy = optional.get();

            // 5) 취소
            if (groupBuy.isBuy()) {
                groupBuy.cancel();
                info.increaseLimitStock();
                return GroupBuyStatus.CANCEL;
            }

            // 6) 재신청
            if (groupBuy.isCancel()) {
                info.decreaseLimitStock();
                groupBuy.buy();
                return GroupBuyStatus.BUY;
            }
            // 7) 미작동, 컴파일 안정용
            throw new IllegalStateException("알 수 없는 상태");
        }
    }
