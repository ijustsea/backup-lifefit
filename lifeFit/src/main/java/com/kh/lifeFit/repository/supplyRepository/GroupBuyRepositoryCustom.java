package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import com.kh.lifeFit.dto.supply.GroupBuySearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupBuyRepositoryCustom {
    Page<GroupBuyInfo> search(GroupBuySearchCond cond, Pageable pageable);
}

