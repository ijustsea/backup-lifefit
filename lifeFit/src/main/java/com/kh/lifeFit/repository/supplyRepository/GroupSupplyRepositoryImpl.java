package com.kh.lifeFit.repository.supplyRepository;

import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import com.kh.lifeFit.domain.supply.QSupply;
import com.kh.lifeFit.domain.supply.QSupplyCategory;
import com.kh.lifeFit.domain.supply.QCategory;
import com.kh.lifeFit.domain.supply.SupplyStatus;
import com.kh.lifeFit.dto.supply.GroupSupplySearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.kh.lifeFit.domain.groupBuy.QGroupBuyInfo.groupBuyInfo;

@Repository
@RequiredArgsConstructor
public class GroupSupplyRepositoryImpl implements GroupSupplyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QSupply supply = QSupply.supply;
    QSupplyCategory supplyCategory = QSupplyCategory.supplyCategory;
    QCategory category = QCategory.category;

    @Override
    public Page<GroupBuyInfo> search(GroupSupplySearchCond cond, Pageable pageable) {

        List<GroupBuyInfo> content = queryFactory
                .selectDistinct(groupBuyInfo)
                .from(groupBuyInfo)
                .join(groupBuyInfo.supply, supply)
                .leftJoin(supplyCategory).on(supplyCategory.supply.eq(supply))
                .leftJoin(supplyCategory.category, category)
                .where(
                        supply.status.eq(SupplyStatus.GROUP),
                        brandIn(cond.getBrand()),
                        typeIn(cond.getType()),
                        priceIn(cond.getPrice()),
                        statusIn(cond.getGroupStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = queryFactory
                .select(groupBuyInfo.countDistinct())
                .from(groupBuyInfo)
                .join(groupBuyInfo.supply, supply)
                .leftJoin(supplyCategory).on(supplyCategory.supply.eq(supply))
                .leftJoin(supplyCategory.category, category)
                .where(
                        supply.status.eq(SupplyStatus.GROUP),
                        brandIn(cond.getBrand()),
                        typeIn(cond.getType()),
                        priceIn(cond.getPrice()),
                        statusIn(cond.getGroupStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /* =======================
        🔥 브랜드 필터
    ======================= */
    private BooleanExpression brandIn(List<String> brands) {
        return (brands == null || brands.isEmpty())
                ? null
                : supply.brand.in(brands);
    }

    /* =======================
        🔥 성분(카테고리) 필터
    ======================= */
    private BooleanExpression typeIn(List<String> types) {
        return (types == null || types.isEmpty())
                ? null
                : category.name.in(types);
    }

    /* =======================
        🔥 가격 필터 OR 묶기
    ======================= */
    private BooleanExpression priceIn(List<String> prices) {

        if (prices == null || prices.isEmpty()) return null;

        BooleanExpression condition = null;

        for (String price : prices) {
            BooleanExpression exp = null;

            switch (price) {
                case "under20":
                    exp = supply.price.loe(20000);
                    break;
                case "20to50":
                    exp = supply.price.between(20000, 50000);
                    break;
                case "over50":
                    exp = supply.price.goe(50000);
                    break;
                case "all":
                    return null;
            }

            condition = (condition == null) ? exp : condition.or(exp);
        }

        return condition;
    }

    /* =======================
        🔥 공동구매 상태 필터
    ======================= */
    private BooleanExpression statusIn(List<String> statuses) {

        if (statuses == null || statuses.isEmpty()) return null;

        LocalDate today = LocalDate.now();
        BooleanExpression condition = null;

        for (String status : statuses) {
            BooleanExpression exp = null;

            switch (status) {
                case "closed": // 재고 0
                    exp = groupBuyInfo.limitStock.eq(0L);
                    break;

                case "cancel": // 재고 > 0 & 기간 지남
                    exp = groupBuyInfo.limitStock.gt(0)
                            .and(groupBuyInfo.endDate.before(today));
                    break;

                case "active": // 재고 > 0 & 기간 남음
                    exp = groupBuyInfo.limitStock.gt(0)
                            .and(groupBuyInfo.endDate.goe(today));
                    break;
            }

            condition = (condition == null) ? exp : condition.or(exp);
        }

        return condition;
    }
}
