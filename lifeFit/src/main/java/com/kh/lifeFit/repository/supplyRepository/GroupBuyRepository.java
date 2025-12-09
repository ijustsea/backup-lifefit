package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBuyRepository extends JpaRepository<GroupBuyInfo, Long>, GroupBuyRepositoryCustom {
}
