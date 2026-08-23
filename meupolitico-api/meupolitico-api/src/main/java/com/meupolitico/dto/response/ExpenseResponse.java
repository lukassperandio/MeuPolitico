package com.meupolitico.dto.response;

import com.meupolitico.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        Long politicianId,
        String politicianName,
        BigDecimal amount,
        LocalDate date,
        ExpenseCategory category,
        String supplier,
        String documentNumber,
        String description,
        String source,
        LocalDateTime createdAt
) {
}