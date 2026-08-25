package com.meupolitico.dto.response;

import com.meupolitico.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long politicianId,
        String politicianName,
        String externalId,
        LocalDate date,
        AttendanceStatus status,
        String sessionType,
        String source,
        LocalDateTime createdAt
) {
}