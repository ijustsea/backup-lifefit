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
@Table(name = "heart_rate_log")
public class HeartRateLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_rate_log_id")
    private Long id;

    @Column(nullable = false)
    private Long userId; // 스냅샷 저장(약한 참조)

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private int partitionNumber; // 파티션 번호

    @Column(nullable = false)
    private int processingTimeMs; // 처리 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessStatus processStatus; // 처리 상태 SUCCESS, FAILURE

    @Column(columnDefinition = "TEXT")
    private String remarks; // 비고

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

}
