    package com.kh.lifeFit.controller;

    import com.kh.lifeFit.dto.supply.GroupSupplyDto;
    import com.kh.lifeFit.dto.supply.GroupSupplySearchCond;
    import com.kh.lifeFit.service.groupSupplyService.GroupSupplyService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api")
    @CrossOrigin(origins = "*")
    public class GroupSupplyController {

        private final GroupSupplyService groupSupplyService;

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

        /** 🔥 상세 조회 */
        @GetMapping("/groupSupply/{id}")
        public GroupSupplyDto getGroupSupplyDetail(@PathVariable Long id) {
            return groupSupplyService.getGroupSupplyDetail(id);
        }
    }
