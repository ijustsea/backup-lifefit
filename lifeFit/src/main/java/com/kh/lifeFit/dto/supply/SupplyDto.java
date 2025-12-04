package com.kh.lifeFit.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SupplyDto {
    private Long id;
    private String name;
    private Long price;
    private Long stock;
    private String brand;
    private Long tablets;
    private String img;
    private List<CategoryDto> categories;
}

