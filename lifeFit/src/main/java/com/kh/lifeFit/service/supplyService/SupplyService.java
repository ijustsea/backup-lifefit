package com.kh.lifeFit.service.supplyService;

import com.kh.lifeFit.domain.supply.*;
import com.kh.lifeFit.dto.supply.CategoryDto;
import com.kh.lifeFit.dto.supply.SupplyDto;
import com.kh.lifeFit.dto.supply.SupplySearchCond;
import com.kh.lifeFit.repository.supplyRepository.SupplyCategoryRepository;
import com.kh.lifeFit.repository.supplyRepository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyCategoryRepository supplyCategoryRepository;

    /* ===============================
        🔥 1) QueryDSL + 페이징 검색
    =============================== */
    public Page<SupplyDto> searchSupplies(SupplySearchCond cond, Pageable pageable) {

        // 1) Supply 목록 조회 (content + count)
        Page<Supply> page = supplyRepository.search(cond, pageable);
        List<Supply> supplies = page.getContent();

        if (supplies.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2) Supply ID 목록 추출 → DISTINCT 보장
        List<Long> supplyIds = supplies.stream()
                .map(Supply::getId)
                .distinct()
                .toList();

        // 3) 카테고리 전체 조회 (쿼리 1회)
        List<SupplyCategory> allCategories =
                supplyCategoryRepository.findBySupplyIdIn(supplyIds);

        // 4) supplyId → CategoryDto 리스트 매핑
        Map<Long, List<CategoryDto>> categoryMap =
                allCategories.stream()
                        .collect(Collectors.groupingBy(
                                sc -> sc.getSupply().getId(),
                                Collectors.mapping(
                                        sc -> new CategoryDto(
                                                sc.getCategory().getId(),
                                                sc.getCategory().getName()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        // 5) DTO 변환 (추가 쿼리 0회)
        List<SupplyDto> dtoList = supplies.stream()
                .map(supply -> convertToDto(supply, categoryMap))
                .toList();

        return new org.springframework.data.domain.PageImpl<>(
                dtoList,
                pageable,
                page.getTotalElements()
        );
    }

    /* ===============================
        🔥 2) NORMAL 전체 조회
    =============================== */
    public List<SupplyDto> getNormalSupplyList() {

        List<Supply> supplies = supplyRepository.findByStatus(SupplyStatus.NORMAL);
        if (supplies.isEmpty()) {
            return List.of();
        }

        List<Long> supplyIds = supplies.stream()
                .map(Supply::getId)
                .distinct()
                .toList();

        List<SupplyCategory> allCategories =
                supplyCategoryRepository.findBySupplyIdIn(supplyIds);

        Map<Long, List<CategoryDto>> categoryMap =
                allCategories.stream()
                        .collect(Collectors.groupingBy(
                                sc -> sc.getSupply().getId(),
                                Collectors.mapping(
                                        sc -> new CategoryDto(
                                                sc.getCategory().getId(),
                                                sc.getCategory().getName()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        return supplies.stream()
                .map(supply -> convertToDto(supply, categoryMap))
                .toList();
    }

    /* ===============================
        🔥 3) 상세 조회
    =============================== */
    public SupplyDto getSupplyDetail(Long id) {

        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));

        List<SupplyCategory> categoryList =
                supplyCategoryRepository.findBySupplyId(supply.getId());

        List<CategoryDto> categories = categoryList.stream()
                .map(sc -> new CategoryDto(
                        sc.getCategory().getId(),
                        sc.getCategory().getName()
                ))
                .toList();

        // 단일 조회는 N+1 없음
        return convertToDto(supply, Map.of(supply.getId(), categories));
    }

    /* =====================================================
        🔥 4) DTO 변환 메서드
    ===================================================== */
    private SupplyDto convertToDto(Supply supply, Map<Long, List<CategoryDto>> categoryMap) {

        List<CategoryDto> categories = categoryMap.getOrDefault(
                supply.getId(),
                List.of()
        );

        return new SupplyDto(
                supply.getId(),
                supply.getName(),
                supply.getPrice(),
                supply.getStock(),
                supply.getBrand(),
                supply.getTablets(),
                supply.getDetail(),
                supply.getImg(),
                supply.getExp(),
                categories
        );
    }



}
