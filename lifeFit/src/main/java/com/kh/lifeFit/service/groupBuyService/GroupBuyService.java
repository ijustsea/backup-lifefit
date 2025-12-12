    package com.kh.lifeFit.service.groupBuyService;

    import com.kh.lifeFit.domain.groupBuy.GroupBuy;
    import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
    import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
    import com.kh.lifeFit.domain.user.User;
    import com.kh.lifeFit.repository.groupBuyRepository.GroupBuyInfoRepository;
    import com.kh.lifeFit.repository.groupBuyRepository.GroupBuyRepository;
    import com.kh.lifeFit.repository.userRepository.UserRepository;
    import jakarta.persistence.OptimisticLockException;
    import lombok.RequiredArgsConstructor;
    import org.springframework.orm.ObjectOptimisticLockingFailureException;
    import org.springframework.retry.annotation.Backoff;
    import org.springframework.retry.annotation.Retryable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    @Service
    @RequiredArgsConstructor
    public class GroupBuyService {
        private final GroupBuyRepository groupBuyRepository;
        private final GroupBuyInfoRepository groupBuyInfoRepository;
        private final UserRepository userRepository;

        @Retryable(
                value = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
                maxAttempts = 3,
                backoff = @Backoff(delay = 50)
        )
        @Transactional
        public boolean participate(Long groupBuyId, Long userId) {

            // 1) 공동구매 대상 조회
            GroupBuyInfo info = groupBuyInfoRepository.findById(groupBuyId)
                    .orElseThrow(() -> new IllegalArgumentException("공동구매 정보를 찾을 수 없습니다."));

            // 2) 재고 부족 시 즉시 false 반환 (재시도 X)
            if(info.getLimitStock() <= 0) {
                return false;
            }

            // 3) 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

            // 4) 재고 감소 (Dirty Checking -> UPDATE 쿼리 + version 증가)
            info.decreaseLimitStock();

            // 5) 참여 기록 저장
            GroupBuy apply = new GroupBuy(user, info, GroupBuyStatus.BUY);
            groupBuyRepository.save(apply);

            // 성공 처리
            return true;
        }
    }
