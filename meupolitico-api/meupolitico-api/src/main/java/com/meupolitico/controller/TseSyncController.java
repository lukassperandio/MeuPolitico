package com.meupolitico.controller;

import com.meupolitico.service.TseAssetImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync/tse")
public class TseSyncController {

    private final TseAssetImportService tseAssetImportService;

    public TseSyncController(TseAssetImportService tseAssetImportService) {
        this.tseAssetImportService = tseAssetImportService;
    }

    @PostMapping("/assets/{year}")
    public ResponseEntity<String> importAssets(@PathVariable int year) {
        if (year != 2022 && year != 2026) {
            return ResponseEntity.badRequest()
                    .body("Only election years 2022 and 2026 are supported.");
        }
        int n = tseAssetImportService.importElectionYear(year);
        return ResponseEntity.ok(
                "TSE assets " + year + " import completed. Matched politicians: " + n);
    }
}