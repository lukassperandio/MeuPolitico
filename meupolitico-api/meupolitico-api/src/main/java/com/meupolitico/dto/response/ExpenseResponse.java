package com.meupolitico.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        Long politicianId,
        String politicianName,
        BigDecimal amount,
        LocalDate date,
        String category,
        String supplier,
        String documentNumber,
        String description,
        String source,
        LocalDateTime createdAt
) {
}