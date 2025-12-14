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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupSupplyService {

    private final GroupSupplyRepository groupSupplyRepository;
    private final SupplyCategoryRepository supplyCategoryRepository;

    /* ============================================
        🔥 1) QueryDSL + 페이징 검색
    ============================================ */
    /* ============================================
        🔥 1) QueryDSL + 페이징 검색 (N+1 제거)
    ============================================ */
    public Page<GroupSupplyDto> searchGroupSupplies(GroupSupplySearchCond cond, Pageable pageable) {

        // 1) GroupBuyInfo + Supply(fetchJoin) 한 번에 조회 → 쿼리 1회
        Page<GroupBuyInfo> page = groupSupplyRepository.search(cond, pageable);

        List<GroupBuyInfo> gbList = page.getContent();

        if (gbList.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2) Supply ID 목록 추출 (쿼리 없음)
        List<Long> supplyIds = gbList.stream()
                .map(gb -> gb.getSupply().getId())
                .distinct()
                .toList();

        // 3) 카테고리 전체 IN 조회 (쿼리 1회)
        List<SupplyCategory> allCategories =
                supplyCategoryRepository.findBySupplyIdIn(supplyIds);

        // 4) supplyId → categoryName 매핑 (메모리 작업)
        Map<Long, List<String>> categoryMap = allCategories.stream()
                .collect(Collectors.groupingBy(
                        sc -> sc.getSupply().getId(),
                        Collectors.mapping(
                                sc -> sc.getCategory().getName(),
                                Collectors.toList()
                        )
                ));

        // 5) DTO 변환 (추가 쿼리 0회)
        List<GroupSupplyDto> dtoList = gbList.stream()
                .map(gb -> convertToDto(gb, categoryMap))
                .toList();

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    /* ============================================
        🔥 2) 단일 상세 조회
    ============================================ */
    public GroupSupplyDto getGroupSupplyDetail(Long id) {

        GroupBuyInfo gb = groupSupplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공동구매 상품을 찾을 수 없습니다. id=" + id));

        Supply supply = gb.getSupply();

        // 단일 조회는 IN 최적화 불필요 → 기존 방식 유지
        List<SupplyCategory> categoryList =
                supplyCategoryRepository.findBySupplyId(supply.getId());

        List<String> categories = categoryList.stream()
                .map(sc -> sc.getCategory().getName())
                .toList();

        return convertToDto(gb, Map.of(supply.getId(), categories));
    }


    /* ============================================
        🔥 3) 엔티티 → DTO 변환 메소드
    ============================================ */
    private GroupSupplyDto convertToDto(GroupBuyInfo gb, Map<Long, List<String>> categoryMap) {

        Supply supply = gb.getSupply();

        // 카테고리 조회
        List<String> categories = categoryMap.getOrDefault(
                supply.getId(),
                List.of()
        );

        return new GroupSupplyDto(
                gb.getId(),                 // 공구 ID
                supply.getName(),           // 제품명
                supply.getPrice(),          // 가격
                supply.getBrand(),          // 브랜드
                gb.getLimitStock(),         // 공구 제한 재고
                gb.getDiscount(),           // 공구 할인율
                gb.getEndDate(),            // 종료 날짜
                supply.getExp(),            // 유통기한
                supply.getImg(),            // 이미지
                supply.getTablets(),        // 알약수
                supply.getDetail(),         // 상세설명
                categories                  // 성분 카테고리
        );
    }
}
