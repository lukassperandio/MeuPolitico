package com.meupolitico.dto.response;

import java.math.BigDecimal;

public record ComparedPoliticianResponse(
        Long id,
        String name,
        String party,
        String state,
        String position,
        BigDecimal totalExpenses,
        Double attendancePercentage,
        BigDecimal latestAssetValue,
        Integer assetYear
) {
}