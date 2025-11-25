package com.kh.lifeFit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Long tablets;

    @Column(nullable = false)
    private LocalDateTime exp;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private String img;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyStatus status;


}
