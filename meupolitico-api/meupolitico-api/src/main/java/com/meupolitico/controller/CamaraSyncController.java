package com.meupolitico.controller;

import com.meupolitico.integration.camara.CamaraDeputyClient;
import com.meupolitico.integration.camara.dto.CamaraDeputySummary;
import com.meupolitico.integration.camara.dto.CamaraExpenseItem;
import com.meupolitico.service.CamaraSyncService;
import com.meupolitico.service.CeapExpenseImportService;
import com.meupolitico.service.ExpenseRecategorizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sync/camara")
public class CamaraSyncController {

    private final CamaraDeputyClient camaraDeputyClient;
    private final CeapExpenseImportService ceapExpenseImportService;
    private final CamaraSyncService camaraSyncService;
    private final ExpenseRecategorizeService expenseRecategorizeService;

    public CamaraSyncController(CamaraDeputyClient camaraDeputyClient,
                                CamaraSyncService camaraSyncService, CeapExpenseImportService ceapExpenseImportService, ExpenseRecategorizeService expenseRecategorizeService
    ) {
        this.camaraDeputyClient = camaraDeputyClient;
        this.camaraSyncService = camaraSyncService;
        this.ceapExpenseImportService = ceapExpenseImportService;
        this.expenseRecategorizeService = expenseRecategorizeService;
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

    @GetMapping("/expenses/preview/{externalId}")
    public ResponseEntity<List<CamaraExpenseItem>> previewExpenses(
            @PathVariable String externalId,
            @RequestParam(defaultValue = "2024") int year
    ) {
        Long deputyId = Long.valueOf(externalId);
        return ResponseEntity.ok(camaraDeputyClient.fetchExpenses(deputyId, year));
    }

    @PostMapping("/expenses/ceap/{year}")
    public ResponseEntity<String> importCeapExpenses(@PathVariable int year) {
        int imported = ceapExpenseImportService.importYear(year);
        return ResponseEntity.ok("CEAP " + year + " import completed. New expenses: " + imported);
    }

    @PostMapping("/expenses/recategorize")
    public ResponseEntity<String> recategorizeExpenses() {
        int updated = expenseRecategorizeService.recategorizeAll();
        return ResponseEntity.ok("Recategorize completed. Updated expenses: " + updated);
    }
}