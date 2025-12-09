package com.kh.lifeFit.domain.sleepManager;

import com.kh.lifeFit.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="sleep_record")
public class SleepRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_record_id")
    private Long id;

    /** 사용자 */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    /** 기록 날짜 */
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    /** 취침 날짜 */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /** 기상 시간 */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /** 총 수면 시간(분) - 자동 계산 */
    @Column(name = "total_minutes", nullable = false)
    private Integer totalMinutes;

    /** 수면 질 점수(1~5) */
    @Column(name = "sleep_quality", nullable = false)
    private int sleepQuality;

    /** 입면까지 걸린 시간(분) */
    @Column(name = "sleep_latency_minutes", nullable = false)
    private int sleepLatency;

    /** 중간에 깬 횟수 */
    @Column(name = "awake_count", nullable = false)
    private int awakeCount;

    /** 꿈을 꾸었는지 여부 */
    @Column(name = "dreamed", nullable = false)
    private Boolean dreamed;

    /** 기상 컨디션 ENUM */
    @Enumerated(EnumType.STRING)
    @Column(name = "morning_feeling", nullable = false)
    private MorningFeeling morningFeeling;


    /** 메모 (옵션) */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    /** 총 수면 분 자동 계산 메서드 */
    public void calculateTotalMinutes() {
        this.totalMinutes = (int) Duration.between(startTime, endTime).toMinutes();
    }
}

