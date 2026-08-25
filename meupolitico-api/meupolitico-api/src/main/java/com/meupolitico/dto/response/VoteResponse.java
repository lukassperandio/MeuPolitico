package com.meupolitico.dto.response;

import com.meupolitico.enums.VoteChoice;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        Long politicianId,
        String politicianName,
        String externalId,
        LocalDate date,
        String proposition,
        String summary,
        VoteChoice vote,
        String result,
        String source,
        LocalDateTime createdAt
) {
}