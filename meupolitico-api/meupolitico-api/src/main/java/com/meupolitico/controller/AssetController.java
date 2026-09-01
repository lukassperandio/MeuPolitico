package com.meupolitico.controller;

import com.meupolitico.dto.request.AssetRequest;
import com.meupolitico.dto.response.AssetEvolutionResponse;
import com.meupolitico.dto.response.AssetResponse;
import com.meupolitico.service.AssetService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Assets", description = "Patrimônio declarado dos políticos e evolução anual")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    @Operation(summary = "Listar patrimônios", description = "Retorna todos os registros de patrimônio declarados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AssetResponse>> findAll() {
        return ResponseEntity.ok(assetService.findAll());
    }

    @GetMapping("/politician/{politicianId}")
    @Operation(summary = "Patrimônio por político", description = "Lista o patrimônio de um político, ordenado por ano")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<List<AssetResponse>> findByPoliticianId(
            @Parameter(description = "ID interno do político") @PathVariable Long politicianId) {
        return ResponseEntity.ok(assetService.findByPoliticianId(politicianId));
    }

    @GetMapping("/politician/{politicianId}/evolution")
    @Operation(summary = "Evolução patrimonial", description = "Série anual com variação absoluta e percentual (para gráficos)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evolução calculada"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<AssetEvolutionResponse> getEvolution(
            @Parameter(description = "ID interno do político") @PathVariable Long politicianId) {
        return ResponseEntity.ok(assetService.getEvolution(politicianId));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca combinada de patrimônio", description = "Filtros opcionais com paginação")
    @ApiResponse(responseCode = "200", description = "Página de resultados")
    public ResponseEntity<Page<AssetResponse>> search(
            @Parameter(description = "ID do político") @RequestParam(required = false) Long politicianId,
            @Parameter(description = "Ano da declaração") @RequestParam(required = false) Integer year,
            @Parameter(description = "Valor mínimo declarado") @RequestParam(required = false) BigDecimal minValue,
            Pageable pageable
    ) {
        return ResponseEntity.ok(assetService.search(politicianId, year, minValue, pageable));
    }

    @GetMapping("/search/year")
    @Operation(summary = "Buscar por ano", description = "Patrimônios declarados em um ano específico")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AssetResponse>> findByYear(
            @Parameter(description = "Ano da declaração", required = true) @RequestParam Integer year) {
        return ResponseEntity.ok(assetService.findByYear(year));
    }

    @GetMapping("/search/min-value")
    @Operation(summary = "Buscar por valor mínimo", description = "Patrimônios com valor declarado maior ou igual ao informado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AssetResponse>> findByMinValue(
            @Parameter(description = "Valor mínimo", required = true) @RequestParam BigDecimal minValue) {
        return ResponseEntity.ok(assetService.findByMinValue(minValue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar patrimônio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<AssetResponse> findById(
            @Parameter(description = "ID do registro de patrimônio") @PathVariable Long id) {
        return ResponseEntity.ok(assetService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar patrimônio", description = "Um registro por político por ano")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou já existe para o ano"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar patrimônio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro ou político não encontrado")
    })
    public ResponseEntity<AssetResponse> update(
            @Parameter(description = "ID do registro") @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir patrimônio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do registro") @PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}