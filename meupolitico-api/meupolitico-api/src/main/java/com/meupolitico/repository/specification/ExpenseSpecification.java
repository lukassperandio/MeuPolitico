package com.meupolitico.repository.specification;

import com.meupolitico.entity.Expense;
import com.meupolitico.enums.ExpenseCategory;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseSpecification {

    public static Specification<Expense> withFilters(
            Long politicianId,
            ExpenseCategory category,
            String supplier,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount) {

        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (politicianId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("politician").get("id"), politicianId));
            }
            if (category != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }
            if (supplier != null && !supplier.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("supplier")), "%" + supplier.toLowerCase() + "%"));
            }
            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (minAmount != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            return predicate;
        };
    }
}