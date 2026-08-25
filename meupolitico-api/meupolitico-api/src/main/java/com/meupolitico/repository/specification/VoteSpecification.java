package com.meupolitico.repository.specification;

import com.meupolitico.entity.Vote;
import com.meupolitico.enums.VoteChoice;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class VoteSpecification {

    public static Specification<Vote> withFilters(
            Long politicianId,
            VoteChoice vote,
            String proposition,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (politicianId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("politician").get("id"), politicianId));
            }
            if (vote != null) {
                predicate = cb.and(predicate, cb.equal(root.get("vote"), vote));
            }
            if (proposition != null && !proposition.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("proposition")), "%" + proposition.toLowerCase() + "%"));
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