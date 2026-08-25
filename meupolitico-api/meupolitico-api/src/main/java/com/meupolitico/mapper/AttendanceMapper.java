package com.meupolitico.mapper;

import com.meupolitico.dto.request.AttendanceRequest;
import com.meupolitico.dto.response.AttendanceResponse;
import com.meupolitico.entity.Attendance;
import com.meupolitico.entity.Politician;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public Attendance toEntity(AttendanceRequest request, Politician politician) {
        Attendance attendance = new Attendance();

        attendance.setPolitician(politician);
        attendance.setExternalId(request.externalId());
        attendance.setDate(request.date());
        attendance.setStatus(request.status());
        attendance.setSessionType(request.sessionType());
        attendance.setSource(request.source());

        return attendance;
    }

    public AttendanceResponse toResponse(Attendance attendance) {
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getPolitician().getId(),
                attendance.getPolitician().getName(),
                attendance.getExternalId(),
                attendance.getDate(),
                attendance.getStatus(),
                attendance.getSessionType(),
                attendance.getSource(),
                attendance.getCreatedAt()
        );
    }
}