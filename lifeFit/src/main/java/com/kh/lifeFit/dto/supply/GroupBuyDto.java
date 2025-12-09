package com.kh.lifeFit.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GroupBuyDto {
    private Long id;
    private String name;
    private Long price;
    private Long stock;
    private String brand;

    private Long limitStock;
    private Long discount;
    private LocalDate endDate;

    private String img;

    private List<String> categories;
}
