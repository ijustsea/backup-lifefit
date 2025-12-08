package com.kh.lifeFit.domain.heartData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "heart_rate_alert")
public class HeartRateAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_rate_alert_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false) // 무결성
    @JoinColumn(name = "heart_rate_data_id", unique = true, nullable = false)
    private HeartRateData heartRateData;

    @Enumerated(EnumType.STRING)
    private HeartRateSeverity severity; // 심박수 심각도 CAUTION, DANGER

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시
}
