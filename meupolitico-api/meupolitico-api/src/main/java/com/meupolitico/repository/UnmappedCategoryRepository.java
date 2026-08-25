package com.meupolitico.repository;

import com.meupolitico.entity.UnmappedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnmappedCategoryRepository extends JpaRepository<UnmappedCategory, Long> {

    Optional<UnmappedCategory> findByStateAndRawCategoryLabel(String state, String rawCategoryLabel);

    List<UnmappedCategory> findAllByOrderByOccurrencesDesc();
}