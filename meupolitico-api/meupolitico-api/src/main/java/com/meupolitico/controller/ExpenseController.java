package com.meupolitico.controller;

import com.meupolitico.dto.request.ExpenseRequest;
import com.meupolitico.dto.response.ExpenseResponse;
import com.meupolitico.enums.ExpenseCategory;
import com.meupolitico.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> findAll() {
        return ResponseEntity.ok(expenseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.findById(id));
    }

    @GetMapping("/politician/{politicianId}")
    public ResponseEntity<List<ExpenseResponse>> findByPoliticianId(@PathVariable Long politicianId) {
        return ResponseEntity.ok(expenseService.findByPoliticianId(politicianId));
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<ExpenseResponse>> findByCategory(@RequestParam ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.findByCategory(category));
    }

    @GetMapping("/search/supplier")
    public ResponseEntity<List<ExpenseResponse>> findBySupplier(@RequestParam String supplier) {
        return ResponseEntity.ok(expenseService.findBySupplier(supplier));
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<ExpenseResponse>> findByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(expenseService.findByDate(date));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<ExpenseResponse>> findByDateRange(@RequestParam LocalDate start,
                                                                 @RequestParam LocalDate end) {
        return ResponseEntity.ok(expenseService.findByDateRange(start, end));
    }

    @GetMapping("/search/min-amount")
    public ResponseEntity<List<ExpenseResponse>> findByMinAmount(@RequestParam BigDecimal amount) {
        return ResponseEntity.ok(expenseService.findByMinAmount(amount));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseResponse>> search(
            @RequestParam(required = false) Long politicianId,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                expenseService.search(politicianId, category, supplier, startDate, endDate, minAmount, pageable)
        );
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}