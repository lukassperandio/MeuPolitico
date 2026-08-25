package com.meupolitico.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AssetEvolutionResponse(
        Long politicianId,
        String politicianName,
        List<AssetEvolutionPoint> points
) {
    public record AssetEvolutionPoint(
            Integer year,
            BigDecimal declaredValue,
            BigDecimal variationAmount,
            Double variationPercentage
    ) {
    }
}