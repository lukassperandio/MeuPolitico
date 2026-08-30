package com.meupolitico.integration.camara.dto;

import java.util.List;

public record CamaraExpenseListResponse(
        List<CamaraExpenseItem> dados,
        List<CamaraLink> links
) {
}