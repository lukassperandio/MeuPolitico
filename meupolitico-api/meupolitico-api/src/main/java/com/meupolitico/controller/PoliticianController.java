package com.meupolitico.controller;

import com.meupolitico.dto.request.PoliticianRequest;
import com.meupolitico.dto.response.PoliticianResponse;
import com.meupolitico.enums.Gender;
import com.meupolitico.service.PoliticianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class PoliticianController {

    private final PoliticianService politicianService;

    public PoliticianController(PoliticianService politicianService) {
        this.politicianService = politicianService;
    }

    @GetMapping
    public ResponseEntity<Page<PoliticianResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(politicianService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoliticianResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(politicianService.findById(id));
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<PoliticianResponse>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(politicianService.findByName(name));
    }

    @GetMapping("/search/party")
    public ResponseEntity<List<PoliticianResponse>> findByParty(@RequestParam String party) {
        return ResponseEntity.ok(politicianService.findByParty(party));
    }

    @GetMapping("/search/state")
    public ResponseEntity<List<PoliticianResponse>> findByState(@RequestParam String state) {
        return ResponseEntity.ok(politicianService.findByState(state));
    }

    @GetMapping("/search/position")
    public ResponseEntity<List<PoliticianResponse>> findByPosition(@RequestParam String position) {
        return ResponseEntity.ok(politicianService.findByPosition(position));
    }

    @GetMapping("/search/gender")
    public ResponseEntity<List<PoliticianResponse>> findByGender(@RequestParam Gender gender) {
        return ResponseEntity.ok(politicianService.findByGender(gender));
    }

    @PostMapping
    public ResponseEntity<PoliticianResponse> create(@Valid @RequestBody PoliticianRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(politicianService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PoliticianResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody PoliticianRequest request) {
        return ResponseEntity.ok(politicianService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        politicianService.delete(id);
        return ResponseEntity.noContent().build();
    }
}