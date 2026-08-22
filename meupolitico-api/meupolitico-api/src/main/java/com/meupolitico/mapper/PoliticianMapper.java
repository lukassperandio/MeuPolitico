package com.meupolitico.mapper;

import com.meupolitico.dto.request.PoliticianRequest;
import com.meupolitico.dto.response.PoliticianResponse;
import com.meupolitico.entity.Politician;
import org.springframework.stereotype.Component;

@Component
public class PoliticianMapper {

    public Politician toEntity(PoliticianRequest request) {
        Politician politician = new Politician();

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

        return politician;
    }

    public PoliticianResponse toResponse(Politician politician) {
        return new PoliticianResponse(
                politician.getId(),
                politician.getExternalId(),
                politician.getName(),
                politician.getBallotName(),
                politician.getPhotoUrl(),
                politician.getParty(),
                politician.getState(),
                politician.getPosition(),
                politician.getStatus(),
                politician.getBirthDate(),
                politician.getGender(),
                politician.getCreatedAt(),
                politician.getUpdatedAt()
        );
    }
}