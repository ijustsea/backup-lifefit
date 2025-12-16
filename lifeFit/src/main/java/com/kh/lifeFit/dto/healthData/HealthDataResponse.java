package com.kh.lifeFit.dto.healthData;

import com.kh.lifeFit.domain.common.Gender;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class HealthDataResponse {

    private Long id;
    private String name;
    private String dept;
    private String recordedDate;
    private String gender;
    private Double bmi;
    private Integer bloodSugar;
    private Integer systolic;
    private Integer diastolic;
    private String checkupDate;

    @QueryProjection
    public HealthDataResponse(Long id, String name, String dept, LocalDateTime recordedDate, Gender gender, Double bmi, Integer bloodSugar, Integer systolic, Integer diastolic, LocalDate checkupDate) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.recordedDate = recordedDate != null ? recordedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.gender = gender != null ? gender.name() : null;
        this.bmi = bmi;
        this.bloodSugar = bloodSugar;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.checkupDate = checkupDate != null ? checkupDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }
}
