package com.meupolitico.dto.request;

import com.meupolitico.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AttendanceRequest(

        @NotNull(message = "Politician ID is required")
        Long politicianId,

        @Size(max = 100, message = "External ID must be at most 100 characters")
        String externalId,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Status is required")
        AttendanceStatus status,

        @Size(max = 50, message = "Session type must be at most 50 characters")
        String sessionType,

        @Size(max = 100, message = "Source must be at most 100 characters")
        String source
) {
}