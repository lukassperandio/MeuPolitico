package com.meupolitico.controller;

import com.meupolitico.dto.response.ComparisonResponse;
import com.meupolitico.service.ComparisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comparisons")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @GetMapping
    public ResponseEntity<ComparisonResponse> compare(@RequestParam String ids) {
        return ResponseEntity.ok(comparisonService.compare(ids));
    }
}