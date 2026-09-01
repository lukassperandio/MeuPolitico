package com.meupolitico.controller;

import com.meupolitico.dto.response.RankingItemResponse;
import com.meupolitico.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@Tag(name = "Rankings", description = "Rankings de gastos, assiduidade e patrimônio")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/expenses")
    @Operation(
            summary = "Ranking por gastos",
            description = "Ordena políticos pela soma de despesas (maior primeiro). Filtros opcionais."
    )
    @ApiResponse(responseCode = "200", description = "Ranking gerado")
    public ResponseEntity<List<RankingItemResponse>> rankByExpenses(
            @Parameter(description = "UF (ex.: SP)") @RequestParam(required = false) String state,
            @Parameter(description = "Partido (ex.: PT)") @RequestParam(required = false) String party,
            @Parameter(description = "Cargo (busca parcial)") @RequestParam(required = false) String position,
            @Parameter(description = "Início do período de gastos") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Fim do período de gastos") @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                rankingService.rankByExpenses(state, party, position, startDate, endDate)
        );
    }

    @GetMapping("/attendance")
    @Operation(
            summary = "Ranking por assiduidade",
            description = "Ordena pelo percentual de presença. secondaryValue = total de sessões."
    )
    @ApiResponse(responseCode = "200", description = "Ranking gerado")
    public ResponseEntity<List<RankingItemResponse>> rankByAttendance(
            @Parameter(description = "UF") @RequestParam(required = false) String state,
            @Parameter(description = "Partido") @RequestParam(required = false) String party,
            @Parameter(description = "Cargo") @RequestParam(required = false) String position,
            @Parameter(description = "Início do período") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Fim do período") @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                rankingService.rankByAttendance(state, party, position, startDate, endDate)
        );
    }

    @GetMapping("/assets")
    @Operation(
            summary = "Ranking por patrimônio",
            description = "Ordena pelo valor declarado (último ano disponível ou ano filtrado)"
    )
    @ApiResponse(responseCode = "200", description = "Ranking gerado")
    public ResponseEntity<List<RankingItemResponse>> rankByAssets(
            @Parameter(description = "UF") @RequestParam(required = false) String state,
            @Parameter(description = "Partido") @RequestParam(required = false) String party,
            @Parameter(description = "Cargo") @RequestParam(required = false) String position,
            @Parameter(description = "Ano da declaração") @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                rankingService.rankByAssets(state, party, position, year)
        );
    }
}