package com.kh.lifeFit.repository.SleepRepository;

import com.kh.lifeFit.domain.sleepManager.SleepRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SleepRecordRepository
        extends JpaRepository<SleepRecord, Long>, SleepRecordRepositoryCustom {
    boolean existsByUserIdAndRecordDate(Long userId, LocalDate recordDate);
}
