package com.kh.lifeFit.repository.SleepRepository;

import com.kh.lifeFit.domain.sleepManager.SleepRecord;

public interface SleepRecordRepositoryCustom {
    SleepRecord saveSleepRecord(SleepRecord record);
}
