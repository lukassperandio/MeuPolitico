package com.meupolitico.integration.camara.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraDeputyDetail(
        Long id,
        String nome,
        String siglaPartido,
        String siglaUf,
        CamaraDeputyStatus ultimoStatus
) {
}