package com.meupolitico.dto.response;

import java.time.LocalDate;

public record AttendanceSummaryResponse(
        Long politicianId,
        String politicianName,
        long totalSessions,
        long present,
        double attendancePercentage,
        LocalDate mandateStart,
        boolean dataComplete
) { }