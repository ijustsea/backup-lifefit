package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.Supply;
import com.kh.lifeFit.domain.supply.SupplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyRepository
        extends JpaRepository<Supply, Long>, SupplyRepositoryCustom {  // ⭐ 추가됨
    List<Supply> findByStatus(SupplyStatus status);
}
