package com.meupolitico.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetResponse(
        Long id,
        Long politicianId,
        String politicianName,
        Integer year,
        BigDecimal declaredValue,
        String source,
        LocalDateTime createdAt
) {
}