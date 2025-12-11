package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import com.kh.lifeFit.dto.supply.GroupSupplySearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupSupplyRepositoryCustom {
    Page<GroupBuyInfo> search(GroupSupplySearchCond cond, Pageable pageable);
}

