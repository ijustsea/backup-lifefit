package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.supply.SupplyDto;
import com.kh.lifeFit.dto.supply.SupplySearchCond;
import com.kh.lifeFit.service.filterService.SupplyFilterService;
import com.kh.lifeFit.service.supplyService.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SupplyController {

    private final SupplyService supplyService;
    private final SupplyFilterService supplyFilterService;
    /**
     * 🔥 QueryDSL 기반 조건 검색 + 페이징.
     * 예: /api/supplies?brand=종근당&type=비타민C&page=0&size=8
     */
    @GetMapping("/supplies")
    public Page<SupplyDto> searchSupplies(
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> price,
            Pageable pageable
    ) {
        SupplySearchCond cond = new SupplySearchCond(brand, type, price);
        return supplyService.searchSupplies(cond, pageable);
    }

    @GetMapping("/supplies/filters")
    public Map<String, List<String>> getSupplyFilters() {
        return Map.of(
                "brands", supplyFilterService.getAllBrands(),
                "types", supplyFilterService.getAllCategoryNames()
        );
    }

    /**
     * 🔥 단일 조회
     * /api/supply/3
     */
    @GetMapping("/supply/{id}")
    public SupplyDto getSupplyById(@PathVariable Long id) {
        return supplyService.getSupplyDetail(id);
    }
}
