package com.kh.lifeFit.domain.heartData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "heart_rate_data")
public class HeartRateData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_rate_data_id")
    private Long id;

    @Column(nullable = false)
    private Long userId; // 사용자 식별자

    @Column(nullable = false)
    private int heartRate; // 심박수

    @Column(nullable = false, updatable = false)
    private LocalDateTime measuredAt; // 측정 일시

    @Enumerated(EnumType.STRING)
    private HeartRateStatus status; // NORMAL, CAUTION, DANGER

    @Column(nullable = false) // 첫 측정이라 비교 대상이 없으면 0으로 저장
    private int variation; // 변화량

    @Column(nullable = false)
    private Long elapsedTimeSeconds; // 경과 시간

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

}
