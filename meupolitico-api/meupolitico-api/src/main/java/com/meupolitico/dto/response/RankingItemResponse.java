package com.meupolitico.dto.response;

import java.math.BigDecimal;

public record RankingItemResponse(
        int position,
        Long politicianId,
        String politicianName,
        String party,
        String state,
        String positionTitle,
        BigDecimal value,
        Double secondaryValue
) {
}