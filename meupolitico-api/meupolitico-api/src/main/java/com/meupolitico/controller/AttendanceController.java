package com.meupolitico.controller;

import com.meupolitico.dto.request.AttendanceRequest;
import com.meupolitico.dto.response.AttendanceResponse;
import com.meupolitico.dto.response.AttendanceSummaryResponse;
import com.meupolitico.enums.AttendanceStatus;
import com.meupolitico.service.AttendanceService;
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
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> findAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }

    @GetMapping("/politician/{politicianId}")
    public ResponseEntity<List<AttendanceResponse>> findByPoliticianId(@PathVariable Long politicianId) {
        return ResponseEntity.ok(attendanceService.findByPoliticianId(politicianId));
    }

    @GetMapping("/politician/{politicianId}/summary")
    public ResponseEntity<AttendanceSummaryResponse> getSummary(
            @PathVariable Long politicianId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(attendanceService.getSummary(politicianId, startDate, endDate));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AttendanceResponse>> search(
            @RequestParam(required = false) Long politicianId,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(required = false) String sessionType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                attendanceService.search(politicianId, status, sessionType, startDate, endDate, pageable)
        );
    }

    @GetMapping("/search/status")
    public ResponseEntity<List<AttendanceResponse>> findByStatus(@RequestParam AttendanceStatus status) {
        return ResponseEntity.ok(attendanceService.findByStatus(status));
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<AttendanceResponse>> findByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(attendanceService.findByDate(date));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<AttendanceResponse>> findByDateRange(@RequestParam LocalDate start,
                                                                    @RequestParam LocalDate end) {
        return ResponseEntity.ok(attendanceService.findByDateRange(start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> create(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}