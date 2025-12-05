package com.kh.lifeFit.controller.sleepManager;

import com.kh.lifeFit.dto.sleep.SleepRecordHabitRequest;
import com.kh.lifeFit.dto.sleep.SleepRecordRequest;
import com.kh.lifeFit.service.SleepService.SleepRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sleep")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://192.168.20.60:5173"
})
public class SleepRecordController {
    private final SleepRecordService sleepRecordService;

    @PostMapping("/record")
    public ResponseEntity<?> saveSleepRecord(@RequestBody SleepRecordRequest request) {

        // userId null 체크 (프론트에서 강제 전달되도록)
        if (request.getUserId() == null || request.getUserId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("userId 값이 필요합니다.");
        }

        Long savedId = sleepRecordService.createSleepRecord(request.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("수면 기록 저장 완료! 기록 ID: " + savedId);
    }

    /** 새로 추가되는 통합 저장 API */
    @PostMapping("/save-all")
    public ResponseEntity<?> saveAll(@RequestBody SleepRecordHabitRequest request) {
        try {
            Long recordId = sleepRecordService.saveRecordAndHabit(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("recordId", recordId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400 에러
                    .body(Map.of("error", e.getMessage()));
        }
    }
}