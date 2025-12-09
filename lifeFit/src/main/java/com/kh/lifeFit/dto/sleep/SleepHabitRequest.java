package com.kh.lifeFit.dto.sleep;

import lombok.Data;

@Data
public class SleepHabitRequest {
    private Long sleepRecordId;   // SleepRecord FK
    private Boolean caffeine;
    private Boolean drink;
    private Boolean exercise;
    private String screenTime;
}