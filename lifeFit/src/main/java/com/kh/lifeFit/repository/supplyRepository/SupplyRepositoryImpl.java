package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.supply.*;
import com.kh.lifeFit.dto.supply.SupplySearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SupplyRepositoryImpl implements SupplyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Supply> search(SupplySearchCond cond, Pageable pageable) {

        QSupply supply = QSupply.supply;
        QSupplyCategory supplyCategory = QSupplyCategory.supplyCategory;
        QCategory category = QCategory.category;

        /** =============================
         *  🔥 1) content 쿼리
         * ============================== */
        List<Supply> content = queryFactory
                .selectDistinct(supply)   // 중복 제거
                .from(supply)
                .leftJoin(supplyCategory).on(supplyCategory.supply.eq(supply))
                .leftJoin(supplyCategory.category, category)
                .where(
                        supply.status.eq(SupplyStatus.NORMAL),
                        brandIn(cond.getBrand()),
                        typeIn(cond.getType()),
                        priceIn(cond.getPrice())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /** =============================
         *  🔥 2) count 쿼리
         * ============================== */
        Long total = queryFactory
                .select(supply.countDistinct())
                .from(supply)
                .leftJoin(supplyCategory).on(supplyCategory.supply.eq(supply))
                .leftJoin(supplyCategory.category, category)
                .where(
                        supply.status.eq(SupplyStatus.NORMAL),
                        brandIn(cond.getBrand()),
                        typeIn(cond.getType()),
                        priceIn(cond.getPrice())
                )
                .fetchOne();

        if (total == null) total = 0L;

        return new PageImpl<>(content, pageable, total);
    }

    /** =============================
     *  🔥 브랜드 필터
     * ============================== */
    private BooleanExpression brandIn(List<String> brands) {
        return (brands == null || brands.isEmpty())
                ? null
                : QSupply.supply.brand.in(brands);
    }

    /** =============================
     *  🔥 카테고리(성분) 필터
     * ============================== */
    private BooleanExpression typeIn(List<String> types) {
        return (types == null || types.isEmpty())
                ? null
                : QCategory.category.name.in(types);
    }

    /** =============================
     *  🔥 가격 필터: OR 조건 묶음
     * ============================== */
    private BooleanExpression priceIn(List<String> prices) {

        if (prices == null || prices.isEmpty()) return null;

        BooleanExpression condition = null;

        for (String price : prices) {
            BooleanExpression exp = null;

            switch (price) {
                case "all":
                    return null;
                case "under20":
                    exp = QSupply.supply.price.loe(20000);
                    break;
                case "20to50":
                    exp = QSupply.supply.price.between(20000, 50000);
                    break;
                case "over50":
                    exp = QSupply.supply.price.goe(50000);
                    break;
            }

            condition = (condition == null) ? exp : condition.or(exp);
        }

        return condition;
    }
}
