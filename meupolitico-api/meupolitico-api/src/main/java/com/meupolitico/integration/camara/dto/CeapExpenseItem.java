package com.meupolitico.integration.camara.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CeapExpenseItem(
        String nomeParlamentar,
        String descricao,
        String fornecedor,
        String cnpjCPF,
        String dataEmissao,
        String valorLiquido,
        Integer idDeputado,
        Integer numeroDeputadoID,
        Long idDocumento,
        String urlDocumento
) {
}