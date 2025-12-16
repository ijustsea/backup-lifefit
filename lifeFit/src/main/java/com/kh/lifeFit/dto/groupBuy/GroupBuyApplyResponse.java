package com.kh.lifeFit.dto.groupBuy;

import com.kh.lifeFit.domain.groupBuy.GroupBuyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupBuyApplyResponse {
    private boolean success;
    private String message;
    private GroupBuyStatus status;
}
