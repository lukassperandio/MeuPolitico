package com.meupolitico.controller;

import com.meupolitico.dto.request.PoliticianRequest;
import com.meupolitico.dto.response.PoliticianResponse;
import com.meupolitico.enums.Gender;
import com.meupolitico.service.PoliticianService;
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

import java.util.List;

@RestController
@RequestMapping("/api/politicians")
@Tag(name = "Politicians", description = "Cadastro e consulta de políticos")
public class PoliticianController {

    private final PoliticianService politicianService;

    public PoliticianController(PoliticianService politicianService) {
        this.politicianService = politicianService;
    }

    @GetMapping
    @Operation(summary = "Listar políticos (paginado)", description = "Use page e size (ex.: page=0&size=20)")
    @ApiResponse(responseCode = "200", description = "Página de políticos")
    public ResponseEntity<Page<PoliticianResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(politicianService.findAll(pageable));
    }

    @GetMapping("/search/name")
    @Operation(summary = "Buscar por nome", description = "Busca parcial, ignore case")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<PoliticianResponse>> findByName(
            @Parameter(description = "Nome ou parte do nome", required = true) @RequestParam String name) {
        return ResponseEntity.ok(politicianService.findByName(name));
    }

    @GetMapping("/search/party")
    @Operation(summary = "Buscar por partido")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<PoliticianResponse>> findByParty(
            @Parameter(description = "Sigla do partido (ex.: PT, PL)", required = true) @RequestParam String party) {
        return ResponseEntity.ok(politicianService.findByParty(party));
    }

    @GetMapping("/search/state")
    @Operation(summary = "Buscar por estado (UF)")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<PoliticianResponse>> findByState(
            @Parameter(description = "UF (ex.: SP, BA)", required = true) @RequestParam String state) {
        return ResponseEntity.ok(politicianService.findByState(state));
    }

    @GetMapping("/search/position")
    @Operation(summary = "Buscar por cargo")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<PoliticianResponse>> findByPosition(
            @Parameter(description = "Cargo (ex.: Deputado Federal)", required = true) @RequestParam String position) {
        return ResponseEntity.ok(politicianService.findByPosition(position));
    }

    @GetMapping("/search/gender")
    @Operation(summary = "Buscar por gênero")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<PoliticianResponse>> findByGender(
            @Parameter(description = "MALE, FEMALE ou NOT_INFORMED", required = true) @RequestParam Gender gender) {
        return ResponseEntity.ok(politicianService.findByGender(gender));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar político por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Político encontrado"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<PoliticianResponse> findById(
            @Parameter(description = "ID interno do político") @PathVariable Long id) {
        return ResponseEntity.ok(politicianService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar político")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PoliticianResponse> create(@Valid @RequestBody PoliticianRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(politicianService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar político")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<PoliticianResponse> update(
            @Parameter(description = "ID do político") @PathVariable Long id,
            @Valid @RequestBody PoliticianRequest request) {
        return ResponseEntity.ok(politicianService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir político")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do político") @PathVariable Long id) {
        politicianService.delete(id);
        return ResponseEntity.noContent().build();
    }
}