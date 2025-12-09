package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.supply.GroupBuyDto;
import com.kh.lifeFit.dto.supply.GroupBuySearchCond;
import com.kh.lifeFit.service.groupBuyService.GroupBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    /** 🔥 공동구매 리스트 + 필터 + 페이징 */
    @GetMapping("/groupBuys")
    public Page<GroupBuyDto> searchGroupBuys(
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> price,
            @RequestParam(required = false) List<String> groupStatus,
            Pageable pageable
    ) {
        GroupBuySearchCond cond = new GroupBuySearchCond(brand, type, price, groupStatus);
        return groupBuyService.searchGroupBuys(cond, pageable);
    }

    /** 🔥 상세 조회 */
    @GetMapping("/groupBuy/{id}")
    public GroupBuyDto getGroupBuyDetail(@PathVariable Long id) {
        return groupBuyService.getGroupBuyDetail(id);
    }
}

