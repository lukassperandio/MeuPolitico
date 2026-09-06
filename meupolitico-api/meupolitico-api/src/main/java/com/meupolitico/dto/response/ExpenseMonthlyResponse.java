package com.meupolitico.dto.response;

import java.util.List;

public record ExpenseMonthlyResponse(
        Long politicianId,
        List<ExpenseMonthTotal> months
) {}
