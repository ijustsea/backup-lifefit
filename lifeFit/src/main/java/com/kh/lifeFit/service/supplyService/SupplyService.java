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

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyCategoryRepository supplyCategoryRepository;

    /* ===============================
        🔥 1) QueryDSL + 페이징 검색
    =============================== */
    public Page<SupplyDto> searchSupplies(SupplySearchCond cond, Pageable pageable) {
        Page<Supply> page = supplyRepository.search(cond, pageable);

        // Supply → SupplyDto 매핑
        return page.map(supply -> {
            List<SupplyCategory> categoryList = supplyCategoryRepository.findBySupplyId(supply.getId());

            List<CategoryDto> categories = categoryList.stream()
                    .map(sc -> new CategoryDto(
                            sc.getCategory().getId(),
                            sc.getCategory().getName()
                    ))
                    .toList();

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
        });
    }

    /* ===============================
        🔥 2) NORMAL 전체 조회
    =============================== */
    public List<SupplyDto> getNormalSupplyList() {
        List<Supply> supplies = supplyRepository.findByStatus(SupplyStatus.NORMAL);

        return supplies.stream().map(supply -> {
            List<SupplyCategory> categoryList = supplyCategoryRepository.findBySupplyId(supply.getId());

            List<CategoryDto> categories = categoryList.stream()
                    .map(sc -> new CategoryDto(
                            sc.getCategory().getId(),
                            sc.getCategory().getName()
                    ))
                    .toList();

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
        }).toList();
    }

    /* ===============================
        🔥 3) 상세 조회
    =============================== */
    public SupplyDto getSupplyDetail(Long id) {
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));

        List<SupplyCategory> categoryList = supplyCategoryRepository.findBySupplyId(supply.getId());

        List<CategoryDto> categories = categoryList.stream()
                .map(sc -> new CategoryDto(
                        sc.getCategory().getId(),
                        sc.getCategory().getName()
                ))
                .toList();

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
