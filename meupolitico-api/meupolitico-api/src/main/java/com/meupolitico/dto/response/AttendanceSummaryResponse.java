package com.meupolitico.dto.response;

public record AttendanceSummaryResponse(
        Long politicianId,
        String politicianName,
        long totalSessions,
        long presentCount,
        long absentCount,
        long justifiedCount,
        double presencePercentage
) {
}