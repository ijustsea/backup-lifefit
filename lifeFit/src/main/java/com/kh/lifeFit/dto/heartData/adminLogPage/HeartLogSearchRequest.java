package com.kh.lifeFit.dto.heartData.adminLogPage;

import com.kh.lifeFit.domain.heartData.ProcessStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class HeartLogSearchRequest {

    private Long userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private ProcessStatus processStatus;

    private Integer partitionNumber;
}
