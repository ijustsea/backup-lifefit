package com.kh.lifeFit.dto.healthData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class HealthDataPageResponse {

    private List<HealthDataResponse> data;
    private PageableInfo pageable;
    private long totalCount;
    private int totalPages;

    public HealthDataPageResponse(List<HealthDataResponse> data, Page<?> pageInfo){
        this.data = data;
        this.totalCount = pageInfo.getTotalElements();
        this.totalPages = pageInfo.getTotalPages();
        this.pageable = new PageableInfo(pageInfo.getNumber(), pageInfo.getSize());
    }

    @Getter
    @AllArgsConstructor
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
    }

}
