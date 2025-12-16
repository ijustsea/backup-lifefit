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

    import java.util.Optional;

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
        public GroupBuyStatus participate(Long groupBuyInfoId, Long userId) {

            // 1) 공동구매 대상 조회
            GroupBuyInfo info = groupBuyInfoRepository.findById(groupBuyInfoId)
                    .orElseThrow(() -> new IllegalArgumentException("공동구매 정보를 찾을 수 없습니다."));

            // 2) 공동구매 참여여부 확인
            Optional<GroupBuy> optional = groupBuyRepository.findByUserIdAndGroupBuyInfoId(userId, groupBuyInfoId);

            // 3) 최초참여
            if (optional.isEmpty()){
                //재고 체크
                if(info.getLimitStock() <=0){return null;}
                //회원 정보 유무 체크
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new IllegalArgumentException("회원정보를 찾을수 없습니다."));
                //공동구매 재고 감소
                info.decreaseLimitStock();
                //공공구매 내역 저장
                groupBuyRepository.save(new GroupBuy(user, info, GroupBuyStatus.BUY));
                return GroupBuyStatus.BUY;
            }
            // 4) 이미 참여존재
            GroupBuy groupBuy = optional.get();

            if(groupBuy.getStatus() == GroupBuyStatus.BUY){
                groupBuy.changeStatus(GroupBuyStatus.CANCEL);
                //재고 복구
                info.increaseLimitStock();
                return GroupBuyStatus.CANCEL;
            }

            // 5) 취소 후 재신청
            if(groupBuy.getStatus() == GroupBuyStatus.CANCEL){
                //재고 체크
                if(info.getLimitStock() <=0){return null;}
                groupBuy.changeStatus(GroupBuyStatus.BUY);
                info.decreaseLimitStock();
                return GroupBuyStatus.BUY;
            }
            // 이론상 미작동, 컴파일 안정용
            return null;
        }
    }
