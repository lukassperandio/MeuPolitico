package com.meupolitico.repository;

import com.meupolitico.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = """
    SELECT DISTINCT ON (a.politician_id)
           a.politician_id,
           a.declared_value,
           a.year
    FROM asset a
    WHERE a.declared_value > 0
    ORDER BY a.politician_id, a.year DESC
    """, nativeQuery = true)
    List<Object[]> findLatestAssetsByPolitician();
}