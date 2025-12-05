package com.kh.lifeFit.service.supplyService;

import com.kh.lifeFit.domain.supply.*;


import com.kh.lifeFit.dto.supply.CategoryDto;
import com.kh.lifeFit.dto.supply.SupplyDto;
import com.kh.lifeFit.repository.supplyRepository.SupplyCategoryRepository;
import com.kh.lifeFit.repository.supplyRepository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyCategoryRepository supplyCategoryRepository;

    // NORMAL 상품 목록 조회
    public List<SupplyDto> getNormalSupplyList() {

        List<Supply> supplies = supplyRepository.findByStatus(SupplyStatus.NORMAL);

        return supplies.stream().map(supply -> {

            // 공급 상품과 연결된 카테고리 조회
            List<SupplyCategory> categoryList =
                    supplyCategoryRepository.findBySupplyId(supply.getId());

            // CategoryDto 변환
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

    // 단일 상품 조회 (상세페이지용)
    public SupplyDto getSupplyDetail(Long id) {

        // 1) Supply 엔티티 조회
        Supply supply = supplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));

        // 2) 해당 상품의 카테고리 목록 조회
        List<SupplyCategory> categoryList =
                supplyCategoryRepository.findBySupplyId(supply.getId());

        // 3) CategoryDto 변환
        List<CategoryDto> categories = categoryList.stream()
                .map(sc -> new CategoryDto(
                        sc.getCategory().getId(),
                        sc.getCategory().getName()
                ))
                .toList();

        // 4) SupplyDto로 반환
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
