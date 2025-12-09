package com.kh.lifeFit.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class SupplySearchCond {
    private List<String> brand;
    private List<String> type;
    private List<String> price;
}
