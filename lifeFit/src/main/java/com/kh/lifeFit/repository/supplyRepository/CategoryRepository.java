package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
        select distinct c.name
        from SupplyCategory sc
        join sc.category c
        join sc.supply s
        where s.status = 'NORMAL'
    """)
    List<String> findDistinctCategoryNames();

    @Query("""
        select distinct c.name
        from SupplyCategory sc
        join sc.category c
        join sc.supply s
        where s.status = 'GROUP'
    """)
    List<String> findDistinctGroupCategoryNames();

}
