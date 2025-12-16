package com.kh.lifeFit.domain.groupBuy;

import com.kh.lifeFit.domain.supply.Supply;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_buy_info_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id", unique = true)
    private Supply supply;

    /** 총 공동구매 수량 (불변) */
    @Column(nullable = false)
    private Long totalStock;

    /** 남은 수량 (변함) */
    @Column(nullable = false)
    private Long limitStock;
    @Column(nullable = false)
    private Long discount;
    @Column(nullable = false)
    private LocalDate endDate;

    @Version
    @Column(nullable = false)
    private Long version;

    public static GroupBuyInfo create(
            Supply supply,
            Long totalStock,
            Long discount,
            LocalDate endDate
    ) {
        if (totalStock <= 0) {
            throw new IllegalArgumentException("totalStock은 1 이상이어야 합니다.");
        }

        GroupBuyInfo info = new GroupBuyInfo();
        info.supply = supply;
        info.totalStock = totalStock;
        info.limitStock = totalStock;
        info.discount = discount;
        info.endDate = endDate;
        return info;
    }

    public void decreaseLimitStock() {
        if (this.limitStock <= 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.limitStock--;
    }

    public void increaseLimitStock() {
        if (this.limitStock >= this.totalStock) {
            return; // ❗ 방어 로직
        }
        this.limitStock++;
    }
}
