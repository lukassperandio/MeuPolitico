package com.meupolitico.integration.camara.dto;

public record CamaraDeputySummary(
        Long id,
        String uri,
        String nome,
        String siglaPartido,
        String siglaUf,
        String urlFoto,
        String email
) {
}