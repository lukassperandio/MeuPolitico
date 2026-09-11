package com.meupolitico.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseTotalsResponse(
        Long politicianId,
        BigDecimal totalAll,
        BigDecimal totalYear,
        int year,
        BigDecimal totalMandate,
        LocalDate mandateStart,
        LocalDate mandateEnd
) {}