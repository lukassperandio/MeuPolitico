package com.meupolitico.integration.camara.dto;

import java.math.BigDecimal;

public record CamaraExpenseItem(
        Integer ano,
        Integer mes,
        String tipoDespesa,
        String codDocumento,
        String tipoDocumento,
        String dataDocumento,
        String numDocumento,
        BigDecimal valorDocumento,
        BigDecimal valorLiquido,
        String nomeFornecedor,
        String cnpjCpfFornecedor,
        String urlDocumento
) {
}