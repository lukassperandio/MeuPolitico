package com.meupolitico.integration.camara.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraAttendanceItem(
        Long idEvento,
        String dataHoraInicio,
        Long idDeputado
) {
}