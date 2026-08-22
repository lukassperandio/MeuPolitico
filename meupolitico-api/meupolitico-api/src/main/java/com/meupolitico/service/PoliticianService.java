package com.meupolitico.service;

import com.meupolitico.dto.request.PoliticianRequest;
import com.meupolitico.dto.response.PoliticianResponse;
import com.meupolitico.entity.Politician;
import com.meupolitico.enums.Gender;
import com.meupolitico.exception.ResourceNotFoundException;
import com.meupolitico.mapper.PoliticianMapper;
import com.meupolitico.repository.PoliticianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoliticianService {

    private final PoliticianRepository politicianRepository;
    private final PoliticianMapper politicianMapper;

    public PoliticianService(PoliticianRepository politicianRepository,
                             PoliticianMapper politicianMapper) {
        this.politicianRepository = politicianRepository;
        this.politicianMapper = politicianMapper;
    }

    public List<PoliticianResponse> findAll() {
        return politicianRepository.findAll()
                .stream()
                .map(politicianMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PoliticianResponse findById(Long id) {
        Politician politician = politicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + id));

        return politicianMapper.toResponse(politician);
    }

    public List<PoliticianResponse> findByName(String name) {
        return politicianRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(politicianMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PoliticianResponse> findByParty(String party) {
        return politicianRepository.findByPartyIgnoreCase(party)
                .stream()
                .map(politicianMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PoliticianResponse> findByState(String state) {
        return politicianRepository.findByStateIgnoreCase(state)
                .stream()
                .map(politicianMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PoliticianResponse> findByPosition(String position) {
        return politicianRepository.findByPositionContainingIgnoreCase(position)
                .stream()
                .map(politicianMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PoliticianResponse> findByGender(Gender gender) {
        return politicianRepository.findByGender(gender)
                .stream()
                .map(politicianMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PoliticianResponse create(PoliticianRequest request) {
        Politician politician = politicianMapper.toEntity(request);
        Politician savedPolitician = politicianRepository.save(politician);

        return politicianMapper.toResponse(savedPolitician);
    }

    public PoliticianResponse update(Long id, PoliticianRequest request) {
        Politician politician = politicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + id));

        politician.setExternalId(request.externalId());
        politician.setName(request.name());
        politician.setBallotName(request.ballotName());
        politician.setPhotoUrl(request.photoUrl());
        politician.setParty(request.party());
        politician.setState(request.state());
        politician.setPosition(request.position());
        politician.setStatus(request.status());
        politician.setBirthDate(request.birthDate());
        politician.setGender(request.gender());

        Politician updatedPolitician = politicianRepository.save(politician);

        return politicianMapper.toResponse(updatedPolitician);
    }

    public void delete(Long id) {
        if (!politicianRepository.existsById(id)) {
            throw new ResourceNotFoundException("Politician not found with id: " + id);
        }

        politicianRepository.deleteById(id);
    }
}