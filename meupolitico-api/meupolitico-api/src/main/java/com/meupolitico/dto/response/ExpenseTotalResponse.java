package com.meupolitico.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseTotalResponse(
        Long politicianId,
        BigDecimal totalAmount,
        long expenseCount,
        LocalDate lastExpenseDate
) {}