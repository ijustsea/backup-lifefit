package com.kh.lifeFit.service.filterService;

import com.kh.lifeFit.repository.supplyRepository.CategoryRepository;
import com.kh.lifeFit.repository.supplyRepository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SupplyFilterService {
    private final SupplyRepository supplyRepository;
    private final CategoryRepository categoryRepository;
    // NORMAL 영양제 브랜드 목록 조회
    public List<String> getAllBrands() {
        return supplyRepository.findDistinctBrands();
    }

    // NORMAL 영양제 카테고리 목록 조회
    public List<String> getAllCategoryNames() {
        return categoryRepository.findDistinctCategoryNames();
    }
}
