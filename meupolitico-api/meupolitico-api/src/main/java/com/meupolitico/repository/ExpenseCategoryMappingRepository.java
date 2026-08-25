package com.meupolitico.repository;

import com.meupolitico.entity.ExpenseCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryMappingRepository extends JpaRepository<ExpenseCategoryMapping, Long> {

    Optional<ExpenseCategoryMapping> findByStateAndRawCategoryLabel(String state, String rawCategoryLabel);

    List<ExpenseCategoryMapping> findByState(String state);
}