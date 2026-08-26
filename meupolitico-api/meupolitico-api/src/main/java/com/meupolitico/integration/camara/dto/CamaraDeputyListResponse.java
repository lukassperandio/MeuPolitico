package com.meupolitico.integration.camara.dto;

import java.util.List;

public record CamaraDeputyListResponse(
        List<CamaraDeputySummary> dados,
        List<CamaraLink> links
) {
}