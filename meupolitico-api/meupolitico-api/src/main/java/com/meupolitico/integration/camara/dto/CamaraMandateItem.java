package com.meupolitico.integration.camara.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraMandateItem(
        Long id,
        String dataInicio,
        String dataFim,
        String situacao,
        String descricaoSituacao
) { }