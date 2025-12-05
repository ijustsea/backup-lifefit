package com.kh.lifeFit.controller;


import com.kh.lifeFit.dto.supply.SupplyDto;
import com.kh.lifeFit.service.supplyService.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supply")
@CrossOrigin(origins = "*") // React 요청 허용
public class SupplyController {

    private final SupplyService supplyService;

    // NORMAL 상품 전체 조회
    @GetMapping
    public List<SupplyDto> getNormalSupplies() {
        return supplyService.getNormalSupplyList();
    }
}
