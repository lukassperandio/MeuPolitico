package com.meupolitico.service;

import com.meupolitico.dto.request.AttendanceRequest;
import com.meupolitico.dto.response.AttendanceResponse;
import com.meupolitico.dto.response.AttendanceSummaryResponse;
import com.meupolitico.entity.Attendance;
import com.meupolitico.entity.Politician;
import com.meupolitico.enums.AttendanceStatus;
import com.meupolitico.exception.ResourceNotFoundException;
import com.meupolitico.mapper.AttendanceMapper;
import com.meupolitico.repository.AttendanceRepository;
import com.meupolitico.repository.PoliticianRepository;
import com.meupolitico.repository.specification.AttendanceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PoliticianRepository politicianRepository;
    private final AttendanceMapper attendanceMapper;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             PoliticianRepository politicianRepository,
                             AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.politicianRepository = politicianRepository;
        this.attendanceMapper = attendanceMapper;
    }

    public List<AttendanceResponse> findAll() {
        return attendanceRepository.findAll()
                .stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AttendanceResponse findById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        return attendanceMapper.toResponse(attendance);
    }

    public List<AttendanceResponse> findByPoliticianId(Long politicianId) {
        if (!politicianRepository.existsById(politicianId)) {
            throw new ResourceNotFoundException("Politician not found with id: " + politicianId);
        }

        return attendanceRepository.findByPoliticianId(politicianId)
                .stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> findByStatus(AttendanceStatus status) {
        return attendanceRepository.findByStatus(status)
                .stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> findByDate(LocalDate date) {
        return attendanceRepository.findByDate(date)
                .stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<AttendanceResponse> search(Long politicianId,
                                           AttendanceStatus status,
                                           String sessionType,
                                           LocalDate startDate,
                                           LocalDate endDate,
                                           Pageable pageable) {
        var spec = AttendanceSpecification.withFilters(
                politicianId, status, sessionType, startDate, endDate);

        return attendanceRepository.findAll(spec, pageable)
                .map(attendanceMapper::toResponse);
    }

    public AttendanceSummaryResponse getSummary(Long politicianId, LocalDate startDate, LocalDate endDate) {
        Politician politician = politicianRepository.findById(politicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + politicianId));

        List<Attendance> records;
        if (startDate != null && endDate != null) {
            records = attendanceRepository.findByPoliticianIdAndDateBetween(politicianId, startDate, endDate);
        } else {
            records = attendanceRepository.findByPoliticianId(politicianId);
        }

        long total = records.size();
        long present = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absent = records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long justified = records.stream().filter(a -> a.getStatus() == AttendanceStatus.JUSTIFIED).count();

        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        return new AttendanceSummaryResponse(
                politician.getId(),
                politician.getName(),
                total,
                present,
                absent,
                justified,
                Math.round(percentage * 100.0) / 100.0
        );
    }

    @Transactional
    public AttendanceResponse create(AttendanceRequest request) {
        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        Attendance attendance = attendanceMapper.toEntity(request, politician);
        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toResponse(saved);
    }

    @Transactional
    public AttendanceResponse update(Long id, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));

        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        attendance.setPolitician(politician);
        attendance.setExternalId(request.externalId());
        attendance.setDate(request.date());
        attendance.setStatus(request.status());
        attendance.setSessionType(request.sessionType());
        attendance.setSource(request.source());

        Attendance updated = attendanceRepository.save(attendance);
        return attendanceMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }
}