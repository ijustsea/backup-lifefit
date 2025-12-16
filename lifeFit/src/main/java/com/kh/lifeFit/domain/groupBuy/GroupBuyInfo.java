package com.kh.lifeFit.domain.groupBuy;

import com.kh.lifeFit.domain.supply.Supply;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class GroupBuyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_buy_info_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_id", unique = true)
    private Supply supply;

    @Column(nullable = false)
    private Long limitStock;
    @Column(nullable = false)
    private Long discount;
    @Column(nullable = false)
    private LocalDate endDate;

    @Version
    @Column(nullable = false)
    private Long version;

    //공동구매 재고 감소 메소드
    public void decreaseLimitStock() {
        this.limitStock = this.limitStock - 1;
    }
    //공동구매 재고 증가 메소드
    public void increaseLimitStock() {
        this.limitStock = this.limitStock + 1;
    }
}
