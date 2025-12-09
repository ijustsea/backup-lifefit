package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.Supply;
import com.kh.lifeFit.dto.supply.SupplySearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplyRepositoryCustom {
    Page<Supply> search(SupplySearchCond cond, Pageable pageable);
}
