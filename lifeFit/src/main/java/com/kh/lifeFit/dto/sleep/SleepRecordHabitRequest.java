package com.kh.lifeFit.dto.sleep;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SleepRecordHabitRequest {
    private SleepRecordRequest record; // 수면 기록
    private SleepHabitRequest habit;   // 생활 습관
}
