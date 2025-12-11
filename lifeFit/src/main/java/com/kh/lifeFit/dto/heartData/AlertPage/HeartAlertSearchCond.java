package com.kh.lifeFit.dto.heartData.AlertPage;

import com.kh.lifeFit.domain.heartData.HeartRateStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class HeartAlertSearchCond {

    private HeartRateStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
