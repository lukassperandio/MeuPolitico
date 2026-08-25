package com.meupolitico.mapper;

import com.meupolitico.dto.request.VoteRequest;
import com.meupolitico.dto.response.VoteResponse;
import com.meupolitico.entity.Politician;
import com.meupolitico.entity.Vote;
import org.springframework.stereotype.Component;

@Component
public class VoteMapper {

    public Vote toEntity(VoteRequest request, Politician politician) {
        Vote vote = new Vote();

        vote.setPolitician(politician);
        vote.setExternalId(request.externalId());
        vote.setDate(request.date());
        vote.setProposition(request.proposition());
        vote.setSummary(request.summary());
        vote.setVote(request.vote());
        vote.setResult(request.result());
        vote.setSource(request.source());

        return vote;
    }

    public VoteResponse toResponse(Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getPolitician().getId(),
                vote.getPolitician().getName(),
                vote.getExternalId(),
                vote.getDate(),
                vote.getProposition(),
                vote.getSummary(),
                vote.getVote(),
                vote.getResult(),
                vote.getSource(),
                vote.getCreatedAt()
        );
    }
}