package com.kh.lifeFit.repository.groupBuyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {

    Optional<GroupBuy> findByUserIdAndGroupBuyInfoId(
            Long userId,
            Long groupBuyInfoId
    );

}

