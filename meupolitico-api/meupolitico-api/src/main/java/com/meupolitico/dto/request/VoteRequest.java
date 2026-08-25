package com.meupolitico.dto.request;

import com.meupolitico.enums.VoteChoice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VoteRequest(

        @NotNull(message = "Politician ID is required")
        Long politicianId,

        @Size(max = 100, message = "External ID must be at most 100 characters")
        String externalId,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotBlank(message = "Proposition is required")
        @Size(max = 300, message = "Proposition must be at most 300 characters")
        String proposition,

        String summary,

        @NotNull(message = "Vote is required")
        VoteChoice vote,

        @Size(max = 100, message = "Result must be at most 100 characters")
        String result,

        @Size(max = 100, message = "Source must be at most 100 characters")
        String source
) {
}