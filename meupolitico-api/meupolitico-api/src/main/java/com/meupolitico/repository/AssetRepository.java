package com.meupolitico.repository;

import com.meupolitico.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>,
        JpaSpecificationExecutor<Asset> {

    List<Asset> findByPoliticianId(Long politicianId);

    List<Asset> findByPoliticianIdOrderByYearAsc(Long politicianId);

    Optional<Asset> findByPoliticianIdAndYear(Long politicianId, Integer year);

    List<Asset> findByYear(Integer year);

    List<Asset> findByDeclaredValueGreaterThanEqual(BigDecimal minValue);
}