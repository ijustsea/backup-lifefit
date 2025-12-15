package com.kh.lifeFit.domain.supply;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Supply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="supply_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column
    private Long stock;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Long tablets;

    @Column(nullable = false)
    private LocalDate exp;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private String img;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyStatus status;


}
