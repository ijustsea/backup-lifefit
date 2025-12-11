package com.kh.lifeFit.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GroupSupplyDto {
    private Long id;
    private String name;
    private Long price;
    private String brand;

    private Long limitStock;
    private Long discount;
    private LocalDate endDate;
    private LocalDateTime exp;
    private String img;

    private Long tablets;
    private String detail;

    private List<String> categories;
}
