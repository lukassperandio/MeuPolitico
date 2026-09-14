package com.meupolitico.integration.camara.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraDeputyStatus(
        String data,
        String situacao,
        String condicaoEleitoral,
        String descricaoStatus
) {
}