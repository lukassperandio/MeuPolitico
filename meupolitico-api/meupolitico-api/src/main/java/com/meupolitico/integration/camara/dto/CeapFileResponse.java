package com.meupolitico.integration.camara.dto;

import java.util.List;

public record CeapFileResponse(
        List<CeapExpenseItem> dados
) {
}