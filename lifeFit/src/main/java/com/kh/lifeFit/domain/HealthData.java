package com.kh.lifeFit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HealthData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String userName;
    private String userDepartment;
    private LocalDateTime recordedDate;

    @Enumerated(EnumType.STRING)
    private Gender userGender; // FEMALE, MALE

    private Double bmi;
    private Double bloodSugar;
    private Double bloodPressure;
    private LocalDateTime lastCheckupDate;

}
