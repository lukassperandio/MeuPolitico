package com.meupolitico.dto.request;

import com.meupolitico.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PoliticianRequest(

        @Size(max = 50, message = "External ID must be at most 50 characters")
        String externalId,

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must be at most 150 characters")
        String name,

        @Size(max = 100, message = "Ballot name must be at most 100 characters")
        String ballotName,

        @Size(max = 500, message = "Photo URL must be at most 500 characters")
        String photoUrl,

        @Size(max = 20, message = "Party must be at most 20 characters")
        String party,

        @Size(max = 2, message = "State must be exactly 2 characters")
        String state,

        @Size(max = 50, message = "Position must be at most 50 characters")
        String position,

        @Size(max = 30, message = "Status must be at most 30 characters")
        String status,

        LocalDate birthDate,

        Gender gender
) {
}