package com.kh.lifeFit.service.SleepService;

import com.kh.lifeFit.domain.sleepManager.SleepHabit;
import com.kh.lifeFit.domain.sleepManager.SleepRecord;
import com.kh.lifeFit.dto.sleep.SleepHabitRequest;
import com.kh.lifeFit.dto.sleep.SleepHabitResponse;
import com.kh.lifeFit.repository.SleepRepository.SleepHabitQueryRepository;
import com.kh.lifeFit.repository.SleepRepository.SleepHabitRepository;
import com.kh.lifeFit.repository.SleepRepository.SleepRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SleepHabitService {
    private final SleepHabitRepository sleepHabitRepository;
    private final SleepHabitQueryRepository sleepHabitQueryRepository;
    private final SleepRecordRepository sleepRecordRepository;

    public Long saveHabit(SleepHabitRequest request) {

        SleepRecord record = sleepRecordRepository.findById(request.getSleepRecordId())
                .orElseThrow(() -> new IllegalArgumentException("SleepRecord not found"));

        SleepHabit habit = SleepHabit.builder()
                .sleepRecord(record)
                .caffeine(request.getCaffeine())
                .drink(request.getDrink())
                .exercise(request.getExercise())
                .screenTime(request.getScreenTime())
                .build();

        sleepHabitRepository.save(habit);
        return habit.getId();
    }

    public SleepHabitResponse getHabit(Long habitId) {
        SleepHabit habit = sleepHabitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found"));

        return new SleepHabitResponse(
                habit.getId(),
                habit.getSleepRecord().getId(),
                habit.getCaffeine(),
                habit.getDrink(),
                habit.getExercise(),
                habit.getScreenTime()
        );
    }

}
