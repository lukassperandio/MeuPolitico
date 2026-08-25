package com.meupolitico.dto.response;

import java.util.List;

public record ComparisonResponse(
        List<ComparedPoliticianResponse> politicians
) {
}