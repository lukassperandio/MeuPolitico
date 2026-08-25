package com.meupolitico.service;

import com.meupolitico.dto.request.AssetRequest;
import com.meupolitico.dto.response.AssetEvolutionResponse;
import com.meupolitico.dto.response.AssetResponse;
import com.meupolitico.entity.Asset;
import com.meupolitico.entity.Politician;
import com.meupolitico.exception.ResourceNotFoundException;
import com.meupolitico.mapper.AssetMapper;
import com.meupolitico.repository.AssetRepository;
import com.meupolitico.repository.PoliticianRepository;
import com.meupolitico.repository.specification.AssetSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;
    private final PoliticianRepository politicianRepository;
    private final AssetMapper assetMapper;

    public AssetService(AssetRepository assetRepository,
                        PoliticianRepository politicianRepository,
                        AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.politicianRepository = politicianRepository;
        this.assetMapper = assetMapper;
    }

    public List<AssetResponse> findAll() {
        return assetRepository.findAll()
                .stream()
                .map(assetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AssetResponse findById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
        return assetMapper.toResponse(asset);
    }

    public List<AssetResponse> findByPoliticianId(Long politicianId) {
        if (!politicianRepository.existsById(politicianId)) {
            throw new ResourceNotFoundException("Politician not found with id: " + politicianId);
        }

        return assetRepository.findByPoliticianIdOrderByYearAsc(politicianId)
                .stream()
                .map(assetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AssetResponse> findByYear(Integer year) {
        return assetRepository.findByYear(year)
                .stream()
                .map(assetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AssetResponse> findByMinValue(BigDecimal minValue) {
        return assetRepository.findByDeclaredValueGreaterThanEqual(minValue)
                .stream()
                .map(assetMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<AssetResponse> search(Long politicianId,
                                      Integer year,
                                      BigDecimal minValue,
                                      Pageable pageable) {
        var spec = AssetSpecification.withFilters(politicianId, year, minValue);
        return assetRepository.findAll(spec, pageable).map(assetMapper::toResponse);
    }

    public AssetEvolutionResponse getEvolution(Long politicianId) {
        Politician politician = politicianRepository.findById(politicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + politicianId));

        List<Asset> assets = assetRepository.findByPoliticianIdOrderByYearAsc(politicianId);
        List<AssetEvolutionResponse.AssetEvolutionPoint> points = new ArrayList<>();

        BigDecimal previousValue = null;

        for (Asset asset : assets) {
            BigDecimal variationAmount = null;
            Double variationPercentage = null;

            if (previousValue != null) {
                variationAmount = asset.getDeclaredValue().subtract(previousValue);

                if (previousValue.compareTo(BigDecimal.ZERO) > 0) {
                    variationPercentage = variationAmount
                            .multiply(BigDecimal.valueOf(100))
                            .divide(previousValue, 2, RoundingMode.HALF_UP)
                            .doubleValue();
                } else {
                    variationPercentage = 0.0;
                }
            }

            points.add(new AssetEvolutionResponse.AssetEvolutionPoint(
                    asset.getYear(),
                    asset.getDeclaredValue(),
                    variationAmount,
                    variationPercentage
            ));

            previousValue = asset.getDeclaredValue();
        }

        return new AssetEvolutionResponse(
                politician.getId(),
                politician.getName(),
                points
        );
    }

    @Transactional
    public AssetResponse create(AssetRequest request) {
        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        assetRepository.findByPoliticianIdAndYear(request.politicianId(), request.year())
                .ifPresent(a -> {
                    throw new IllegalArgumentException(
                            "Asset already exists for politician " + request.politicianId() + " in year " + request.year());
                });

        Asset asset = assetMapper.toEntity(request, politician);
        Asset saved = assetRepository.save(asset);
        return assetMapper.toResponse(saved);
    }

    @Transactional
    public AssetResponse update(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        assetRepository.findByPoliticianIdAndYear(request.politicianId(), request.year())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Asset already exists for politician " + request.politicianId() + " in year " + request.year());
                    }
                });

        asset.setPolitician(politician);
        asset.setYear(request.year());
        asset.setDeclaredValue(request.declaredValue());
        asset.setSource(request.source());

        Asset updated = assetRepository.save(asset);
        return assetMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asset not found with id: " + id);
        }
        assetRepository.deleteById(id);
    }
}