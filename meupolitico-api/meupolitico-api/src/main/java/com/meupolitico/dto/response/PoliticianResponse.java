package com.meupolitico.dto.response;

import com.meupolitico.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PoliticianResponse(
        Long id,
        String externalId,
        String name,
        String ballotName,
        String photoUrl,
        String party,
        String state,
        String position,
        String status,
        LocalDate birthDate,
        Gender gender,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}