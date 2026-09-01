package com.meupolitico.controller;

import com.meupolitico.dto.request.VoteRequest;
import com.meupolitico.dto.response.VoteResponse;
import com.meupolitico.enums.VoteChoice;
import com.meupolitico.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/votes")
@Tag(name = "Votes", description = "Votações nominais dos políticos")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping
    @Operation(summary = "Listar votações")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<VoteResponse>> findAll() {
        return ResponseEntity.ok(voteService.findAll());
    }

    @GetMapping("/politician/{politicianId}")
    @Operation(summary = "Votações por político")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<List<VoteResponse>> findByPoliticianId(
            @Parameter(description = "ID interno do político") @PathVariable Long politicianId) {
        return ResponseEntity.ok(voteService.findByPoliticianId(politicianId));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca combinada de votações", description = "Filtros opcionais com paginação")
    @ApiResponse(responseCode = "200", description = "Página de resultados")
    public ResponseEntity<Page<VoteResponse>> search(
            @Parameter(description = "ID do político") @RequestParam(required = false) Long politicianId,
            @Parameter(description = "YES, NO, ABSTENTION, OBSTRUCTION, ABSENT") @RequestParam(required = false) VoteChoice vote,
            @Parameter(description = "Texto da proposição (parcial)") @RequestParam(required = false) String proposition,
            @Parameter(description = "Data inicial") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Data final") @RequestParam(required = false) LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                voteService.search(politicianId, vote, proposition, startDate, endDate, pageable)
        );
    }

    @GetMapping("/search/vote")
    @Operation(summary = "Buscar por tipo de voto")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<VoteResponse>> findByVote(
            @Parameter(description = "Tipo de voto", required = true) @RequestParam VoteChoice vote) {
        return ResponseEntity.ok(voteService.findByVote(vote));
    }

    @GetMapping("/search/date")
    @Operation(summary = "Buscar por data exata")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<VoteResponse>> findByDate(
            @Parameter(description = "Data (YYYY-MM-DD)", required = true) @RequestParam LocalDate date) {
        return ResponseEntity.ok(voteService.findByDate(date));
    }

    @GetMapping("/search/date-range")
    @Operation(summary = "Buscar por período")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<VoteResponse>> findByDateRange(
            @Parameter(description = "Data inicial", required = true) @RequestParam LocalDate start,
            @Parameter(description = "Data final", required = true) @RequestParam LocalDate end) {
        return ResponseEntity.ok(voteService.findByDateRange(start, end));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar votação por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<VoteResponse> findById(
            @Parameter(description = "ID do registro") @PathVariable Long id) {
        return ResponseEntity.ok(voteService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar registro de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<VoteResponse> create(@Valid @RequestBody VoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro ou político não encontrado")
    })
    public ResponseEntity<VoteResponse> update(
            @Parameter(description = "ID do registro") @PathVariable Long id,
            @Valid @RequestBody VoteRequest request) {
        return ResponseEntity.ok(voteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do registro") @PathVariable Long id) {
        voteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}