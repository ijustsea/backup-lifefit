package com.kh.lifeFit.repository.groupBuyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupBuyInfoRepository extends JpaRepository<GroupBuyInfo, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from GroupBuyInfo g where g.id = :id")
    Optional<GroupBuyInfo> findByIdForUpdate(Long id);
}
