package com.kh.lifeFit.controller;

import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
import com.kh.lifeFit.dto.groupBuy.GroupBuyApplyResponse;
import com.kh.lifeFit.jwt.CustomUserDetails;
import com.kh.lifeFit.service.groupBuyService.GroupBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groupbuy")
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @PostMapping("/apply/{groupBuyInfoId}")
    public ResponseEntity<?> applyGroupBuy(
            @PathVariable Long groupBuyInfoId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        Long userId = customUserDetails.getUserId();

        GroupBuyStatus status = groupBuyService.participate(groupBuyInfoId, userId);

        if(status == null){
            return ResponseEntity.status(409).body(
                    new GroupBuyApplyResponse(
                            false,
                            "재고가 부족합니다.",
                            null
                    )
            );
        }

        String message = (status == GroupBuyStatus.BUY) ? "공동구매 신청" :"공동구매 취소";

        return ResponseEntity.ok(
                new GroupBuyApplyResponse(
                        true,
                        message,
                        status
                )
        );
    }
}
