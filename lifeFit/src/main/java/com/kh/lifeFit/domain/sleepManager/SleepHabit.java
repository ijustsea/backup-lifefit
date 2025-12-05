package com.kh.lifeFit.domain.sleepManager;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sleep_habit")
public class SleepHabit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_habit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sleep_record_id", nullable = false)
    private SleepRecord sleepRecord;

    /** 취침 전 카페인 섭취 여부 */
    @Column(name = "caffeine", nullable = false)
    private Boolean caffeine;

    /** 음주 여부 */
    @Column(name = "drink", nullable = false)
    private Boolean drink;

    /** 취침 전 운동 여부 */
    @Column(name = "exercise", nullable = false)
    private Boolean exercise;

    /** 전자기기 사용 시간 (예: "30분", "1시간") */
    @Column(name = "screen_time")
    private String screenTime;
}
