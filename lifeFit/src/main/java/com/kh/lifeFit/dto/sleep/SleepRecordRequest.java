package com.kh.lifeFit.dto.sleep;

import com.kh.lifeFit.domain.sleepManager.MorningFeeling;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SleepRecordRequest {
    private Long userId;
    private LocalDate recordDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int sleepQuality;
    private int sleepLatency;
    private int awakeCount;
    private Boolean dreamed;
    private MorningFeeling morningFeeling;
    private String note;
}
