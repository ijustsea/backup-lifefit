package com.kh.lifeFit.service.groupSupplyService;

import com.kh.lifeFit.domain.groupBuy.GroupBuyInfo;
import com.kh.lifeFit.domain.supply.Supply;
import com.kh.lifeFit.domain.supply.SupplyCategory;
import com.kh.lifeFit.dto.supply.GroupSupplyDto;
import com.kh.lifeFit.dto.supply.GroupSupplySearchCond;
import com.kh.lifeFit.repository.supplyRepository.GroupSupplyRepository;
import com.kh.lifeFit.repository.supplyRepository.SupplyCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupSupplyService {

    private final GroupSupplyRepository groupSupplyRepository;
    private final SupplyCategoryRepository supplyCategoryRepository;

    /* ============================================
        🔥 1) QueryDSL + 페이징 검색
    ============================================ */
    public Page<GroupSupplyDto> searchGroupSupplies(GroupSupplySearchCond cond, Pageable pageable) {

        Page<GroupBuyInfo> page = groupSupplyRepository.search(cond, pageable);

        return page.map(gb -> convertToDto(gb));
    }

    /* ============================================
        🔥 2) 단일 상세 조회
    ============================================ */
    public GroupSupplyDto getGroupSupplyDetail(Long id) {

        GroupBuyInfo gb = groupSupplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공동구매 상품을 찾을 수 없습니다. id=" + id));

        return convertToDto(gb);
    }


    /* ============================================
        🔥 3) 엔티티 → DTO 변환 메소드
    ============================================ */
    private GroupSupplyDto convertToDto(GroupBuyInfo gb) {

        Supply supply = gb.getSupply();

        // 카테고리 조회
        List<SupplyCategory> categoryList = supplyCategoryRepository.findBySupplyId(supply.getId());

        List<String> categories = categoryList.stream()
                .map(sc -> sc.getCategory().getName())
                .toList();

        return new GroupSupplyDto(
                gb.getId(),                 // 공구 ID
                supply.getName(),           // 제품명
                supply.getPrice(),          // 가격
                supply.getStock(),          // 재고
                supply.getBrand(),          // 브랜드
                gb.getLimitStock(),         // 공구 제한 재고
                gb.getDiscount(),           // 공구 할인율
                gb.getEndDate(),            // 종료 날짜
                supply.getImg(),            // 이미지
                categories                  // 성분 카테고리
        );
    }
}
