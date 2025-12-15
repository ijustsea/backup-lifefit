package com.kh.lifeFit.dto.healthData;

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
    private double bmi;
    private int bloodSugar;
    private int systolic;
    private int diastolic;
    private String checkupDate;

    public HealthDataResponse(Long id, String name, String dept, LocalDateTime recordedDate, String gender, double bmi, int bloodSugar, int systolic, int diastolic, LocalDate checkupDate) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.recordedDate = recordedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.gender = gender;
        this.bmi = bmi;
        this.bloodSugar = bloodSugar;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.checkupDate = checkupDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
