package com.meupolitico.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(

        @NotNull(message = "Politician ID is required")
        Long politicianId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Date is required")
        LocalDate date,

        @Size(max = 100, message = "Category must be at most 100 characters")
        String category,

        @Size(max = 200, message = "Supplier must be at most 200 characters")
        String supplier,

        @Size(max = 20, message = "Document number must be at most 20 characters")
        String documentNumber,

        String description,

        @Size(max = 100, message = "Source must be at most 100 characters")
        String source
) {
}