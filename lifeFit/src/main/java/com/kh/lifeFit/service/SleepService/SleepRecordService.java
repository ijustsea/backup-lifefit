package com.kh.lifeFit.service.SleepService;

import com.kh.lifeFit.domain.sleepManager.SleepHabit;
import com.kh.lifeFit.domain.sleepManager.SleepRecord;
import com.kh.lifeFit.domain.user.User;
import com.kh.lifeFit.dto.sleep.SleepHabitRequest;
import com.kh.lifeFit.dto.sleep.SleepRecordHabitRequest;
import com.kh.lifeFit.dto.sleep.SleepRecordRequest;
import com.kh.lifeFit.repository.SleepRepository.SleepHabitRepository;
import com.kh.lifeFit.repository.SleepRepository.SleepRecordRepository;
import com.kh.lifeFit.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class SleepRecordService {
    private final SleepRecordRepository sleepRecordRepository;
    private final SleepHabitRepository sleepHabitRepository;
    private final UserRepository userRepository;

    public Long createSleepRecord(Long userId, SleepRecordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾지 못 했습니다"));

        // 총 수면 시간 계산 (분 단위)
        long totalMinutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        SleepRecord record = SleepRecord.builder()
                .user(user)
                .recordDate(request.getRecordDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalMinutes((int) totalMinutes)
                .sleepQuality(request.getSleepQuality())
                .sleepLatency(request.getSleepLatency())
                .awakeCount(request.getAwakeCount())
                .dreamed(request.getDreamed())
                .morningFeeling(request.getMorningFeeling())
                .note(request.getNote())
                .build();

        sleepRecordRepository.saveSleepRecord(record);

        return record.getId();

    }

    public Long saveRecordAndHabit(SleepRecordHabitRequest request) {

        SleepRecordRequest recordReq = request.getRecord();
        SleepHabitRequest habitReq = request.getHabit();

        // 🟦 중복 체크: 동일 user + 동일 날짜 여부 확인
        if (sleepRecordRepository.existsByUserIdAndRecordDate(
                recordReq.getUserId(),
                recordReq.getRecordDate()
        )) {
            throw new IllegalArgumentException("이미 해당 날짜에 수면 기록이 존재합니다.");
        }

        // 1) 사용자 조회
        User user = userRepository.findById(recordReq.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2) 총 수면 시간 계산
        int totalMinutes = (int) Duration.between(
                recordReq.getStartTime(), recordReq.getEndTime()
        ).toMinutes();

        // 3) SleepRecord 저장
        SleepRecord record = SleepRecord.builder()
                .user(user)
                .recordDate(recordReq.getRecordDate())
                .startTime(recordReq.getStartTime())
                .endTime(recordReq.getEndTime())
                .totalMinutes(totalMinutes)
                .sleepQuality(recordReq.getSleepQuality())
                .sleepLatency(recordReq.getSleepLatency())
                .awakeCount(recordReq.getAwakeCount())
                .dreamed(recordReq.getDreamed())
                .morningFeeling(recordReq.getMorningFeeling())
                .note(recordReq.getNote())
                .build();

        sleepRecordRepository.save(record);

        // 4) SleepHabit 저장
        SleepHabit habit = SleepHabit.builder()
                .sleepRecord(record)
                .caffeine(habitReq.getCaffeine())
                .drink(habitReq.getDrink())
                .exercise(habitReq.getExercise())
                .screenTime(habitReq.getScreenTime())
                .build();

        sleepHabitRepository.save(habit);

        return record.getId();
    }

}

