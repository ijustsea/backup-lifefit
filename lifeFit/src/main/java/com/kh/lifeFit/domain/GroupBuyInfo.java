package com.kh.lifeFit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Version
    @Column(nullable = false)
    private Long version;
}
