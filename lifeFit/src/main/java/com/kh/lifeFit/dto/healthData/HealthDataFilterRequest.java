package com.kh.lifeFit.dto.healthData;

import com.kh.lifeFit.domain.common.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class HealthDataFilterRequest {

    private String name;
    private String dept;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate; // 조회 시작일
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate; // 조회 종료일
    private Gender gender; // MALE || FEMALE
    private String bmi;
    private String bloodSugar;
    private String bloodPressure;
    private String checkupDate;

}
