package com.meupolitico.controller;

import com.meupolitico.dto.response.RankingItemResponse;
import com.meupolitico.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rankings")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<RankingItemResponse>> rankByExpenses(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                rankingService.rankByExpenses(state, party, position, startDate, endDate)
        );
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<RankingItemResponse>> rankByAttendance(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                rankingService.rankByAttendance(state, party, position, startDate, endDate)
        );
    }

    @GetMapping("/assets")
    public ResponseEntity<List<RankingItemResponse>> rankByAssets(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                rankingService.rankByAssets(state, party, position, year)
        );
    }
}