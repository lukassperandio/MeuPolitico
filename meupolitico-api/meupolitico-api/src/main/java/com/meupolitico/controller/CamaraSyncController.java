package com.meupolitico.controller;

import com.meupolitico.integration.camara.CamaraDeputyClient;
import com.meupolitico.integration.camara.dto.CamaraDeputySummary;
import com.meupolitico.integration.camara.dto.CamaraExpenseItem;
import com.meupolitico.service.CamaraSyncService;
import com.meupolitico.service.CeapExpenseImportService;
import com.meupolitico.service.ExpenseRecategorizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Câmara Sync", description = "Sincronização e importação de dados da Câmara dos Deputados (admin)")
public class CamaraSyncController {

    private final CamaraDeputyClient camaraDeputyClient;
    private final CeapExpenseImportService ceapExpenseImportService;
    private final CamaraSyncService camaraSyncService;
    private final ExpenseRecategorizeService expenseRecategorizeService;

    public CamaraSyncController(CamaraDeputyClient camaraDeputyClient,
                                CamaraSyncService camaraSyncService,
                                CeapExpenseImportService ceapExpenseImportService,
                                ExpenseRecategorizeService expenseRecategorizeService) {
        this.camaraDeputyClient = camaraDeputyClient;
        this.camaraSyncService = camaraSyncService;
        this.ceapExpenseImportService = ceapExpenseImportService;
        this.expenseRecategorizeService = expenseRecategorizeService;
    }

    @GetMapping("/deputies/preview")
    @Operation(
            summary = "Preview de deputados",
            description = "Busca deputados na API da Câmara sem salvar no banco (debug)"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada da API externa")
    public ResponseEntity<List<CamaraDeputySummary>> previewDeputies() {
        return ResponseEntity.ok(camaraDeputyClient.fetchAllDeputies());
    }

    @PostMapping("/deputies")
    @Operation(
            summary = "Sincronizar deputados",
            description = "Busca todos os deputados na Câmara e cria/atualiza Politician pelo externalId"
    )
    @ApiResponse(responseCode = "200", description = "Sync concluído")
    public ResponseEntity<String> syncDeputies() {
        int count = camaraSyncService.syncDeputies();
        return ResponseEntity.ok("Sync completed. Politicians saved/updated: " + count);
    }

    @GetMapping("/expenses/preview/{externalId}")
    @Operation(
            summary = "Preview de despesas (API)",
            description = "Consulta /deputados/{id}/despesas na Câmara. Pode retornar vazio se o endpoint externo estiver indisponível — preferir CEAP."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada (pode ser vazia)")
    public ResponseEntity<List<CamaraExpenseItem>> previewExpenses(
            @Parameter(description = "ID do deputado na Câmara (externalId)") @PathVariable String externalId,
            @Parameter(description = "Ano das despesas") @RequestParam(defaultValue = "2024") int year
    ) {
        Long deputyId = Long.valueOf(externalId);
        return ResponseEntity.ok(camaraDeputyClient.fetchExpenses(deputyId, year));
    }

    @PostMapping("/expenses/ceap/{year}")
    @Operation(
            summary = "Importar despesas CEAP",
            description = "Baixa Ano-{year}.json.zip da Câmara, importa gastos e evita duplicatas por externalId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Importação concluída"),
            @ApiResponse(responseCode = "500", description = "Falha no download ou parse do arquivo")
    })
    public ResponseEntity<String> importCeapExpenses(
            @Parameter(description = "Ano do arquivo CEAP (ex.: 2024)") @PathVariable int year) {
        int imported = ceapExpenseImportService.importYear(year);
        return ResponseEntity.ok("CEAP " + year + " import completed. New expenses: " + imported);
    }

    @PostMapping("/expenses/recategorize")
    @Operation(
            summary = "Recategorizar despesas",
            description = "Reprocessa categorias com mappings + heurística (corrige registros OTHER)"
    )
    @ApiResponse(responseCode = "200", description = "Recategorização concluída")
    public ResponseEntity<String> recategorizeExpenses() {
        int updated = expenseRecategorizeService.recategorizeAll();
        return ResponseEntity.ok("Recategorize completed. Updated expenses: " + updated);
    }
}