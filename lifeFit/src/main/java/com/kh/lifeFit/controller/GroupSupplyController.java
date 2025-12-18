    package com.kh.lifeFit.controller;

    import com.kh.lifeFit.dto.supply.GroupSupplyDto;
    import com.kh.lifeFit.dto.supply.GroupSupplySearchCond;
    import com.kh.lifeFit.jwt.CustomUserDetails;
    import com.kh.lifeFit.service.filterService.GroupSupplyFilterService;
    import com.kh.lifeFit.service.groupSupplyService.GroupSupplyService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api")
    public class GroupSupplyController {

        private final GroupSupplyService groupSupplyService;
        private final GroupSupplyFilterService groupSupplyFilterService;

        /** 🔥 공동구매(공구 영양제) 리스트 + 필터 + 페이징 */
        @GetMapping("/groupSupplies")
        public Page<GroupSupplyDto> searchGroupSupplies(
                @RequestParam(required = false) List<String> brand,
                @RequestParam(required = false) List<String> type,
                @RequestParam(required = false) List<String> price,
                @RequestParam(required = false) List<String> groupStatus,
                Pageable pageable
        ) {
            GroupSupplySearchCond cond = new GroupSupplySearchCond(brand, type, price, groupStatus);
            return groupSupplyService.searchGroupSupplies(cond, pageable);
        }

        @GetMapping("/groupSupplies/filters")
        public Map<String, List<String>> getGroupSupplyFilters() {
            return Map.of(
                    "brands", groupSupplyFilterService.getAllGroupBrands(),
                    "types", groupSupplyFilterService.getAllGroupCategoryNames()
            );
        }

        /** 🔥 상세 조회 */
        @GetMapping("/groupSupply/{id}")
        public GroupSupplyDto getGroupSupplyDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
            Long userId = (userDetails != null) ? userDetails.getUserId() : null;
            return groupSupplyService.getGroupSupplyDetail(id,  userId);
        }
    }
