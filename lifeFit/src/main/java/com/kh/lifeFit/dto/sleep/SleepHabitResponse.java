package com.kh.lifeFit.dto.sleep;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SleepHabitResponse {
    private Long habitId;
    private Long sleepRecordId;
    private Boolean caffeine;
    private Boolean drink;
    private Boolean exercise;
    private String screenTime;
}
