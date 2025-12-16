package com.kh.lifeFit.dto.supply;

import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GroupSupplyDto {
    private Long id;
    private String name;
    private Long price;
    private String brand;

    private Long totalStock;
    private Long limitStock;
    private Long discount;
    private LocalDate endDate;
    private LocalDate exp;
    private String img;

    private Long tablets;
    private String detail;

    private List<String> categories;
    private GroupBuyStatus myGroupBuyStatus;
}
