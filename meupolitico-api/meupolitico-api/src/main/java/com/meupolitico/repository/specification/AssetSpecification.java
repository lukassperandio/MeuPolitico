package com.meupolitico.repository.specification;

import com.meupolitico.entity.Asset;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class AssetSpecification {

    public static Specification<Asset> withFilters(
            Long politicianId,
            Integer year,
            BigDecimal minValue
    ) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (politicianId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("politician").get("id"), politicianId));
            }
            if (year != null) {
                predicate = cb.and(predicate, cb.equal(root.get("year"), year));
            }
            if (minValue != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("declaredValue"), minValue));
            }

            return predicate;
        };
    }
}