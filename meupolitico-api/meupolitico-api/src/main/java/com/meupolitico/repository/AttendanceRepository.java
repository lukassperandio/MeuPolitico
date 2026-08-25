package com.meupolitico.repository;

import com.meupolitico.entity.Attendance;
import com.meupolitico.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>,
        JpaSpecificationExecutor<Attendance> {

    List<Attendance> findByPoliticianId(Long politicianId);

    List<Attendance> findByStatus(AttendanceStatus status);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Attendance> findByPoliticianIdAndDateBetween(Long politicianId, LocalDate startDate, LocalDate endDate);
}