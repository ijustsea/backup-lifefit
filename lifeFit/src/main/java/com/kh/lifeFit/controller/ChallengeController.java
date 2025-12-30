package com.kh.lifeFit.controller;

import com.kh.lifeFit.dto.challenge.ChallengeDetailResponse;
import com.kh.lifeFit.dto.challenge.ChallengeListItemResponse;
import com.kh.lifeFit.dto.challenge.ChallengeSummaryResponse;
import com.kh.lifeFit.service.challengeService.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService service;

    @GetMapping("/api/challenges/list")
    public List<ChallengeListItemResponse> getChallengeList () {
        return service.getChallengeList();
    }

    @GetMapping("/api/challenges/summary")
    public ChallengeSummaryResponse getChallengeSummary () {
        return service.getChallengeSummary();
    }

    @GetMapping("/api/challenges/{id}")
    public ResponseEntity<ChallengeDetailResponse>  getChallengeDetail (@PathVariable Long id) {

        return service.getChallengeDetail(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }



}
