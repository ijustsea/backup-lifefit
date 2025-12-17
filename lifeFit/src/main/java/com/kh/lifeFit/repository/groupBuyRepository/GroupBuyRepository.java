package com.kh.lifeFit.repository.groupBuyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuy;
import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {

    Optional<GroupBuy> findByUserIdAndGroupBuyInfoId(
            Long userId,
            Long groupBuyInfoId
    );

    // 동시성 테스트
    long countByGroupBuyInfoIdAndStatus(
            Long groupBuyInfoId,
            GroupBuyStatus status
    );
}

