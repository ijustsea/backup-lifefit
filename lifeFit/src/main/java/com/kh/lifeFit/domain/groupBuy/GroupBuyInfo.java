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

    @OneToOne
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
}
