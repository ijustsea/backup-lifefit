package com.kh.lifeFit.domain.heartData;

import com.kh.lifeFit.domain.common.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
//@Setter // 상태 변경은 비즈니스 메서드로만 가능하게 막음.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "heart_rate_data")
public class HeartRateData { // 측정 데이터으로 항상 존재한다.

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
    private HeartRateStatus status; // NORMAL, CAUTION, DANGER (로직에 의해 결정)

    @Column(nullable = false) // 첫 측정이라 비교 대상이 없으면 0으로 저장
    private int variation; // 변화량

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시


    @Builder
    public HeartRateData(
            Long userId,
            int heartRate,
            LocalDateTime measuredAt,
            int variation,
            int age,
            Gender gender) {

        this.userId = userId;
        this.heartRate = heartRate;
        this.measuredAt = measuredAt;
        this.variation = variation;

        // Enum 내부에 있는 로직 호출
        this.status = HeartRateStatus.getHeartRateStatus(heartRate, age, gender);
    }

}
