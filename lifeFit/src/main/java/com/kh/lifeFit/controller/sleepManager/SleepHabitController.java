package com.kh.lifeFit.controller.sleepManager;

import com.kh.lifeFit.dto.sleep.SleepHabitRequest;
import com.kh.lifeFit.dto.sleep.SleepHabitResponse;
import com.kh.lifeFit.service.SleepService.SleepHabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sleep")
public class SleepHabitController {
    private final SleepHabitService sleepHabitService;

    @PostMapping("/habit")
    public Long saveHabit(@RequestBody SleepHabitRequest request) {
        return sleepHabitService.saveHabit(request);
    }

    @GetMapping("/{habitId}")
    public SleepHabitResponse getHabit(@PathVariable Long habitId) {
        return sleepHabitService.getHabit(habitId);
    }

}
