package com.meupolitico.controller;

import com.meupolitico.integration.camara.CamaraDeputyClient;
import com.meupolitico.integration.camara.dto.CamaraDeputySummary;
import com.meupolitico.service.CamaraSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sync/camara")
public class CamaraSyncController {

    private final CamaraDeputyClient camaraDeputyClient;

    private final CamaraSyncService camaraSyncService;

    public CamaraSyncController(CamaraDeputyClient camaraDeputyClient,
                                CamaraSyncService camaraSyncService) {
        this.camaraDeputyClient = camaraDeputyClient;
        this.camaraSyncService = camaraSyncService;
    }

    @GetMapping("/deputies/preview")
    public ResponseEntity<List<CamaraDeputySummary>> previewDeputies() {
        return ResponseEntity.ok(camaraDeputyClient.fetchAllDeputies());
    }

    @PostMapping("/deputies")
    public ResponseEntity<String> syncDeputies() {
        int count = camaraSyncService.syncDeputies();
        return ResponseEntity.ok("Sync completed. Politicians saved/updated: " + count);
    }
}