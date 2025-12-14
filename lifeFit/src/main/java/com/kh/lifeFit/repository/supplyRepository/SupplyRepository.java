package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.Supply;
import com.kh.lifeFit.domain.supply.SupplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplyRepository
        extends JpaRepository<Supply, Long>, SupplyRepositoryCustom {  // ⭐ 추가됨
    List<Supply> findByStatus(SupplyStatus status);

    @Query("select distinct s.brand from Supply s where s.status = 'NORMAL'")
    List<String> findDistinctBrands();

    @Query("select distinct s.brand from Supply s where s.status = 'GROUP'")
    List<String> findDistinctGroupBrands();
}
