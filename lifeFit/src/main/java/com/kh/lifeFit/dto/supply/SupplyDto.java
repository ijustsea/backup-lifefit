package com.kh.lifeFit.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private String detail;
    private String img;
    private LocalDateTime exp;
    private List<CategoryDto> categories;
}

