package com.kh.lifeFit.domain.healthData;

import com.kh.lifeFit.domain.common.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HealthData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_data_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    private String userDepartment;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime recordedDate; // 시스템 기록일

    @Enumerated(EnumType.STRING)
    private Gender userGender; // FEMALE, MALE

    private Double bmi;
    private Double bloodSugar;
    private Integer systolic; // 수축기
    private Integer diastolic; // 이완기

    private LocalDate checkupDate; // 검진일

}
