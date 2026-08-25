package com.meupolitico.service;

import com.meupolitico.dto.request.VoteRequest;
import com.meupolitico.dto.response.VoteResponse;
import com.meupolitico.entity.Politician;
import com.meupolitico.entity.Vote;
import com.meupolitico.enums.VoteChoice;
import com.meupolitico.exception.ResourceNotFoundException;
import com.meupolitico.mapper.VoteMapper;
import com.meupolitico.repository.PoliticianRepository;
import com.meupolitico.repository.VoteRepository;
import com.meupolitico.repository.specification.VoteSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final PoliticianRepository politicianRepository;
    private final VoteMapper voteMapper;

    public VoteService(VoteRepository voteRepository,
                       PoliticianRepository politicianRepository,
                       VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.politicianRepository = politicianRepository;
        this.voteMapper = voteMapper;
    }

    public List<VoteResponse> findAll() {
        return voteRepository.findAll()
                .stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VoteResponse findById(Long id) {
        Vote vote = voteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + id));
        return voteMapper.toResponse(vote);
    }

    public List<VoteResponse> findByPoliticianId(Long politicianId) {
        if (!politicianRepository.existsById(politicianId)) {
            throw new ResourceNotFoundException("Politician not found with id: " + politicianId);
        }

        return voteRepository.findByPoliticianId(politicianId)
                .stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<VoteResponse> search(Long politicianId,
                                     VoteChoice vote,
                                     String proposition,
                                     LocalDate startDate,
                                     LocalDate endDate,
                                     Pageable pageable) {
        var spec = VoteSpecification.withFilters(politicianId, vote, proposition, startDate, endDate);
        return voteRepository.findAll(spec, pageable).map(voteMapper::toResponse);
    }

    public VoteResponse create(VoteRequest request) {
        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        Vote vote = voteMapper.toEntity(request, politician);
        Vote saved = voteRepository.save(vote);
        return voteMapper.toResponse(saved);
    }

    public VoteResponse update(Long id, VoteRequest request) {
        Vote vote = voteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + id));

        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        vote.setPolitician(politician);
        vote.setExternalId(request.externalId());
        vote.setDate(request.date());
        vote.setProposition(request.proposition());
        vote.setSummary(request.summary());
        vote.setVote(request.vote());
        vote.setResult(request.result());
        vote.setSource(request.source());

        Vote updated = voteRepository.save(vote);
        return voteMapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!voteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vote not found with id: " + id);
        }
        voteRepository.deleteById(id);
    }

    public List<VoteResponse> findByVote(VoteChoice vote) {
        return voteRepository.findByVote(vote)
                .stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<VoteResponse> findByDate(LocalDate date) {
        return voteRepository.findByDate(date)
                .stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<VoteResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return voteRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }
}