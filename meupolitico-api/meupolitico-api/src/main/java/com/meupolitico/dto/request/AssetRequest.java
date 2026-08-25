package com.meupolitico.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AssetRequest(

        @NotNull(message = "Politician ID is required")
        Long politicianId,

        @NotNull(message = "Year is required")
        @Min(value = 1900, message = "Year must be at least 1900")
        @Max(value = 2100, message = "Year must be at most 2100")
        Integer year,

        @NotNull(message = "Declared value is required")
        @DecimalMin(value = "0.00", message = "Declared value must be zero or positive")
        BigDecimal declaredValue,

        @Size(max = 100, message = "Source must be at most 100 characters")
        String source
) {
}