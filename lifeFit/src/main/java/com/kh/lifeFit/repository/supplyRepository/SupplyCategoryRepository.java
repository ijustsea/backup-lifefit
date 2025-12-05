package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.SupplyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyCategoryRepository extends JpaRepository<SupplyCategory, Long> {
    List<SupplyCategory> findBySupplyId(Long supplyId);
}
