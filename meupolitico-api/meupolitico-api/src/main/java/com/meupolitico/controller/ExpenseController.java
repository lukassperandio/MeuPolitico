package com.meupolitico.controller;

import com.meupolitico.dto.request.ExpenseRequest;
import com.meupolitico.dto.response.ExpenseResponse;
import com.meupolitico.enums.ExpenseCategory;
import com.meupolitico.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Gastos parlamentares (CEAP e lançamentos manuais)")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @Operation(summary = "Listar despesas (paginado)", description = "Lista geral — use page e size (ex.: page=0&size=20)")
    @ApiResponse(responseCode = "200", description = "Página de despesas")
    public ResponseEntity<Page<ExpenseResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(expenseService.findAll(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca combinada de despesas", description = "Filtros opcionais com paginação")
    @ApiResponse(responseCode = "200", description = "Página de resultados")
    public ResponseEntity<Page<ExpenseResponse>> search(
            @Parameter(description = "ID do político") @RequestParam(required = false) Long politicianId,
            @Parameter(description = "Categoria da despesa") @RequestParam(required = false) ExpenseCategory category,
            @Parameter(description = "Fornecedor (parcial, ignore case)") @RequestParam(required = false) String supplier,
            @Parameter(description = "Data inicial") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Data final") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Valor mínimo") @RequestParam(required = false) BigDecimal minAmount,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                expenseService.search(politicianId, category, supplier, startDate, endDate, minAmount, pageable)
        );
    }

    @GetMapping("/politician/{politicianId}")
    @Operation(summary = "Despesas por político")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<List<ExpenseResponse>> findByPoliticianId(
            @Parameter(description = "ID interno do político") @PathVariable Long politicianId) {
        return ResponseEntity.ok(expenseService.findByPoliticianId(politicianId));
    }

    @GetMapping("/search/category")
    @Operation(summary = "Buscar por categoria")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<ExpenseResponse>> findByCategory(
            @Parameter(description = "Categoria", required = true) @RequestParam ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.findByCategory(category));
    }

    @GetMapping("/search/supplier")
    @Operation(summary = "Buscar por fornecedor")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<ExpenseResponse>> findBySupplier(
            @Parameter(description = "Nome do fornecedor", required = true) @RequestParam String supplier) {
        return ResponseEntity.ok(expenseService.findBySupplier(supplier));
    }

    @GetMapping("/search/date")
    @Operation(summary = "Buscar por data exata")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<ExpenseResponse>> findByDate(
            @Parameter(description = "Data (YYYY-MM-DD)", required = true) @RequestParam LocalDate date) {
        return ResponseEntity.ok(expenseService.findByDate(date));
    }

    @GetMapping("/search/date-range")
    @Operation(summary = "Buscar por período")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<ExpenseResponse>> findByDateRange(
            @Parameter(description = "Data inicial", required = true) @RequestParam LocalDate start,
            @Parameter(description = "Data final", required = true) @RequestParam LocalDate end) {
        return ResponseEntity.ok(expenseService.findByDateRange(start, end));
    }

    @GetMapping("/search/min-amount")
    @Operation(summary = "Buscar por valor mínimo")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<ExpenseResponse>> findByMinAmount(
            @Parameter(description = "Valor mínimo", required = true) @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(expenseService.findByMinAmount(amount));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar despesa por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Despesa encontrada"),
            @ApiResponse(responseCode = "404", description = "Despesa não encontrada")
    })
    public ResponseEntity<ExpenseResponse> findById(
            @Parameter(description = "ID da despesa") @PathVariable Long id) {
        return ResponseEntity.ok(expenseService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar despesa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar despesa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Despesa ou político não encontrado")
    })
    public ResponseEntity<ExpenseResponse> update(
            @Parameter(description = "ID da despesa") @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir despesa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Despesa não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da despesa") @PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}