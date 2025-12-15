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
public class HeartRateAlert { // 비정상 데이터만 가져오는 이벤트로 조건부로 존재한다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_rate_alert_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false) // 무결성
    @JoinColumn(name = "heart_rate_data_id", unique = true, nullable = false)
    private HeartRateData heartRateData;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    // 스냅샷 사용
    // 도메인 제약 조건으로 심박수 알림 도메인이 비정상 상태에서만 존재한다고 강제했다.
    public HeartRateAlert(HeartRateData data){

        // 검증 : 비정상이 아니면(정상이면) 알림을 만들지 않기
        if (!data.getStatus().isAbnormal()){
            throw  new IllegalArgumentException("정상(NORMAL) 데이터는 해당 알림을 생성할 수 없습니다.");
        }
        // 값 할당
        this.heartRateData = data;
    }

}
