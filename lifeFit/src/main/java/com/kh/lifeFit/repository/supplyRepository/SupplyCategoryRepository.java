package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.SupplyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyCategoryRepository extends JpaRepository<SupplyCategory, Long> {
    List<SupplyCategory> findBySupplyId(Long supplyId);

    //전체 카테고리 목록 조회 (N+1 문제 제거)
    List<SupplyCategory> findBySupplyIdIn(List<Long> supplyIds);
}
