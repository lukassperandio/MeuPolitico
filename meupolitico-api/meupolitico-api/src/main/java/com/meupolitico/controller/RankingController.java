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
    @Operation(summary = "Ranking por gastos")
    @ApiResponse(responseCode = "200", description = "Ranking gerado")
    public ResponseEntity<List<RankingItemResponse>> rankByExpenses(
            @Parameter(description = "UF") @RequestParam(required = false) String state,
            @Parameter(description = "Partido") @RequestParam(required = false) String party,
            @Parameter(description = "Cargo") @RequestParam(required = false) String position,
            @Parameter(description = "Nome (parcial)") @RequestParam(required = false) String name,
            @Parameter(description = "Início") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Fim") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "asc ou desc") @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        return ResponseEntity.ok(
                rankingService.rankByExpenses(state, party, position, name, startDate, endDate, order)
        );
    }

    @GetMapping("/attendance")
    @Operation(summary = "Ranking por assiduidade")
    @ApiResponse(responseCode = "200", description = "Ranking gerado")
    public ResponseEntity<List<RankingItemResponse>> rankByAttendance(
            @Parameter(description = "UF") @RequestParam(required = false) String state,
            @Parameter(description = "Partido") @RequestParam(required = false) String party,
            @Parameter(description = "Cargo") @RequestParam(required = false) String position,
            @Parameter(description = "Nome (parcial)") @RequestParam(required = false) String name,
            @Parameter(description = "Início") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Fim") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "asc ou desc") @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        return ResponseEntity.ok(
                rankingService.rankByAttendance(state, party, position, name, startDate, endDate, order)
        );
    }

    @GetMapping("/assets")
    @Operation(summary = "Ranking por patrimônio")
    @ApiResponse(responseCode = "200", description = "Ranking gerado")
    public ResponseEntity<List<RankingItemResponse>> rankByAssets(
            @Parameter(description = "UF") @RequestParam(required = false) String state,
            @Parameter(description = "Partido") @RequestParam(required = false) String party,
            @Parameter(description = "Cargo") @RequestParam(required = false) String position,
            @Parameter(description = "Nome (parcial)") @RequestParam(required = false) String name,
            @Parameter(description = "Ano da declaração") @RequestParam(required = false) Integer year,
            @Parameter(description = "asc ou desc") @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        return ResponseEntity.ok(
                rankingService.rankByAssets(state, party, position, name, year, order)
        );
    }
}