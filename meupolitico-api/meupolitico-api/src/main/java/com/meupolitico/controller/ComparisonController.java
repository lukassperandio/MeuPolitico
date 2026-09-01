package com.meupolitico.controller;

import com.meupolitico.dto.response.ComparisonResponse;
import com.meupolitico.service.ComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comparisons")
@Tag(name = "Comparisons", description = "Comparação lado a lado de 1 a 3 políticos")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @GetMapping
    @Operation(
            summary = "Comparar políticos",
            description = "Compara gastos totais, assiduidade e último patrimônio. Informe de 1 a 3 IDs separados por vírgula."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparação gerada"),
            @ApiResponse(responseCode = "400", description = "Formato inválido ou quantidade fora de 1–3"),
            @ApiResponse(responseCode = "404", description = "Algum político não encontrado")
    })
    public ResponseEntity<ComparisonResponse> compare(
            @Parameter(
                    description = "IDs internos separados por vírgula",
                    example = "1,2,3",
                    required = true
            )
            @RequestParam String ids) {
        return ResponseEntity.ok(comparisonService.compare(ids));
    }
}