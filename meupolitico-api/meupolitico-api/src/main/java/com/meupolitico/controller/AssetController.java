package com.meupolitico.controller;

import com.meupolitico.dto.request.AssetRequest;
import com.meupolitico.dto.response.AssetEvolutionResponse;
import com.meupolitico.dto.response.AssetResponse;
import com.meupolitico.service.AssetService;
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
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> findAll() {
        return ResponseEntity.ok(assetService.findAll());
    }

    @GetMapping("/politician/{politicianId}")
    public ResponseEntity<List<AssetResponse>> findByPoliticianId(@PathVariable Long politicianId) {
        return ResponseEntity.ok(assetService.findByPoliticianId(politicianId));
    }

    @GetMapping("/politician/{politicianId}/evolution")
    public ResponseEntity<AssetEvolutionResponse> getEvolution(@PathVariable Long politicianId) {
        return ResponseEntity.ok(assetService.getEvolution(politicianId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AssetResponse>> search(
            @RequestParam(required = false) Long politicianId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) BigDecimal minValue,
            Pageable pageable
    ) {
        return ResponseEntity.ok(assetService.search(politicianId, year, minValue, pageable));
    }

    @GetMapping("/search/year")
    public ResponseEntity<List<AssetResponse>> findByYear(@RequestParam Integer year) {
        return ResponseEntity.ok(assetService.findByYear(year));
    }

    @GetMapping("/search/min-value")
    public ResponseEntity<List<AssetResponse>> findByMinValue(@RequestParam BigDecimal minValue) {
        return ResponseEntity.ok(assetService.findByMinValue(minValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}