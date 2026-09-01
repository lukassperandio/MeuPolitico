package com.meupolitico.controller;

import com.meupolitico.dto.request.AttendanceRequest;
import com.meupolitico.dto.response.AttendanceResponse;
import com.meupolitico.dto.response.AttendanceSummaryResponse;
import com.meupolitico.enums.AttendanceStatus;
import com.meupolitico.service.AttendanceService;
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
@RequestMapping("/api/attendances")
@Tag(name = "Attendances", description = "Presença em sessões e resumo de assiduidade")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    @Operation(summary = "Listar presenças", description = "Retorna todos os registros de presença")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AttendanceResponse>> findAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }

    @GetMapping("/politician/{politicianId}")
    @Operation(summary = "Presenças por político")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<List<AttendanceResponse>> findByPoliticianId(
            @Parameter(description = "ID interno do político") @PathVariable Long politicianId) {
        return ResponseEntity.ok(attendanceService.findByPoliticianId(politicianId));
    }

    @GetMapping("/politician/{politicianId}/summary")
    @Operation(
            summary = "Resumo de assiduidade",
            description = "Totais de presença/falta/justificada e percentual de presença (período opcional)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumo calculado"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<AttendanceSummaryResponse> getSummary(
            @Parameter(description = "ID interno do político") @PathVariable Long politicianId,
            @Parameter(description = "Data inicial do período") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Data final do período") @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(attendanceService.getSummary(politicianId, startDate, endDate));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca combinada de presenças", description = "Filtros opcionais com paginação")
    @ApiResponse(responseCode = "200", description = "Página de resultados")
    public ResponseEntity<Page<AttendanceResponse>> search(
            @Parameter(description = "ID do político") @RequestParam(required = false) Long politicianId,
            @Parameter(description = "Status: PRESENT, ABSENT, JUSTIFIED") @RequestParam(required = false) AttendanceStatus status,
            @Parameter(description = "Tipo de sessão (busca parcial)") @RequestParam(required = false) String sessionType,
            @Parameter(description = "Data inicial") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Data final") @RequestParam(required = false) LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                attendanceService.search(politicianId, status, sessionType, startDate, endDate, pageable)
        );
    }

    @GetMapping("/search/status")
    @Operation(summary = "Buscar por status de presença")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AttendanceResponse>> findByStatus(
            @Parameter(description = "PRESENT, ABSENT ou JUSTIFIED", required = true)
            @RequestParam AttendanceStatus status) {
        return ResponseEntity.ok(attendanceService.findByStatus(status));
    }

    @GetMapping("/search/date")
    @Operation(summary = "Buscar por data exata")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AttendanceResponse>> findByDate(
            @Parameter(description = "Data da sessão (YYYY-MM-DD)", required = true)
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(attendanceService.findByDate(date));
    }

    @GetMapping("/search/date-range")
    @Operation(summary = "Buscar por período")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AttendanceResponse>> findByDateRange(
            @Parameter(description = "Data inicial", required = true) @RequestParam LocalDate start,
            @Parameter(description = "Data final", required = true) @RequestParam LocalDate end) {
        return ResponseEntity.ok(attendanceService.findByDateRange(start, end));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar presença por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<AttendanceResponse> findById(
            @Parameter(description = "ID do registro de presença") @PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar registro de presença")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Político não encontrado")
    })
    public ResponseEntity<AttendanceResponse> create(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de presença")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro ou político não encontrado")
    })
    public ResponseEntity<AttendanceResponse> update(
            @Parameter(description = "ID do registro") @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de presença")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do registro") @PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}