package com.kh.lifeFit.repository.SleepRepository;

import com.kh.lifeFit.domain.sleepManager.QSleepHabit;
import com.kh.lifeFit.domain.sleepManager.QSleepRecord;
import com.kh.lifeFit.domain.sleepManager.SleepHabit;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SleepHabitQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<SleepHabit> findByRecordId(Long recordId) {
        QSleepHabit habit = QSleepHabit.sleepHabit;
        QSleepRecord record = QSleepRecord.sleepRecord;

        return queryFactory
                .select(habit)
                .from(habit)
                .join(habit.sleepRecord, record).fetchJoin()
                .where(record.id.eq(recordId))
                .fetch();
    }
}

