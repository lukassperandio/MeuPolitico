package com.meupolitico.repository.specification;

import com.meupolitico.entity.Attendance;
import com.meupolitico.enums.AttendanceStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AttendanceSpecification {

    public static Specification<Attendance> withFilters(
            Long politicianId,
            AttendanceStatus status,
            String sessionType,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (politicianId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("politician").get("id"), politicianId));
            }
            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (sessionType != null && !sessionType.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("sessionType")), "%" + sessionType.toLowerCase() + "%"));
            }
            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            return predicate;
        };
    }
}