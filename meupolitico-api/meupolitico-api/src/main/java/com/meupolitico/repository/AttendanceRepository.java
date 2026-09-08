package com.meupolitico.repository;

import com.meupolitico.entity.Attendance;
import com.meupolitico.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
    SELECT a.politician_id AS politician_id,
           COUNT(*)::float AS total,
           COUNT(*) FILTER (WHERE a.status = 'PRESENT')::float AS present
    FROM attendance a
    GROUP BY a.politician_id
    HAVING COUNT(*) > 0
    ORDER BY (COUNT(*) FILTER (WHERE a.status = 'PRESENT')::float / COUNT(*)::float) DESC
    LIMIT 50
    """, nativeQuery = true)
    List<Object[]> findTopAttendancePercentages();

    boolean existsByExternalId(String externalId);

    @Query("select a.externalId from Attendance a where a.externalId is not null")
    List<String> findAllExternalIds();

    @Query(value = """
    SELECT COUNT(*) FROM (
        SELECT DISTINCT a.date, a.session_type
        FROM attendance a
    ) s
    """, nativeQuery = true)
    long countDistinctEvents();

    @Query(value = """
    SELECT a.politician_id,
           COUNT(*) AS present_count
    FROM (
        SELECT DISTINCT politician_id, date, session_type
        FROM attendance
        WHERE status = 'PRESENT'
    ) a
    GROUP BY a.politician_id
    """, nativeQuery = true)
    List<Object[]> countPresentGroupedByPolitician();

    @Query(value = """
    SELECT COUNT(*) FROM (
        SELECT DISTINCT a.date, a.session_type
        FROM attendance a
        WHERE (CAST(:start AS date) IS NULL OR a.date >= CAST(:start AS date))
          AND (CAST(:end AS date) IS NULL OR a.date <= CAST(:end AS date))
    ) s
    """, nativeQuery = true)
    long countDistinctEventsBetween(@Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    @Query(value = """
    SELECT COUNT(*) FROM (
        SELECT DISTINCT date, session_type
        FROM attendance
        WHERE politician_id = :politicianId
          AND status = 'PRESENT'
    ) s
    """, nativeQuery = true)
    long countDistinctPresentSessions(@Param("politicianId") Long politicianId);
}