package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.groupBuy.GroupBuyApplyRequest;
import com.kh.lifeFit.dto.groupBuy.GroupBuyApplyResponse;
import com.kh.lifeFit.service.groupBuyService.GroupBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groupbuy")
public class GroupBuyController {
    private final GroupBuyService groupBuyService;

    @PostMapping("/apply/{groupBuyInfoId}")
    public ResponseEntity<?> applyGroupBuy(
            @PathVariable Long groupBuyInfoId,
            @RequestBody GroupBuyApplyRequest request) {

        boolean success = groupBuyService.participate(groupBuyInfoId, request.getUserId());

        if (success) {
            return ResponseEntity.ok(
                    new GroupBuyApplyResponse(true, "공동구매 성공")
            );
        } else {
            return ResponseEntity.status(409).body(
                    new GroupBuyApplyResponse(false, "재고 부족 또는 동시성 충돌")
            );
        }
    }
}
