package com.meupolitico.repository;

import com.meupolitico.entity.Expense;
import com.meupolitico.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>,
        JpaSpecificationExecutor<Expense> {

    List<Expense> findByPoliticianId(Long politicianId);

    List<Expense> findByCategory(ExpenseCategory category);

    List<Expense> findBySupplierContainingIgnoreCase(String supplier);

    List<Expense> findByDate(LocalDate date);

    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByAmountGreaterThanEqual(BigDecimal amount);

    boolean existsByExternalId(String externalId);
    Optional<Expense> findByExternalId(String externalId);

    @Query("select e.externalId from Expense e where e.externalId is not null")
    List<String> findAllExternalIds();

    @Query(value = """
        SELECT * FROM expense e
        WHERE (CAST(:politicianId AS bigint) IS NULL OR e.politician_id = CAST(:politicianId AS bigint))
          AND (CAST(:category AS varchar) IS NULL OR e.category = CAST(:category AS varchar))
          AND (CAST(:supplier AS varchar) IS NULL OR LOWER(e.supplier) LIKE LOWER(CONCAT('%', CAST(:supplier AS varchar), '%')))
          AND (CAST(:startDate AS date) IS NULL OR e.date >= CAST(:startDate AS date))
          AND (CAST(:endDate AS date) IS NULL OR e.date <= CAST(:endDate AS date))
          AND (CAST(:minAmount AS numeric) IS NULL OR e.amount >= CAST(:minAmount AS numeric))
        """, nativeQuery = true)
    List<Expense> search(
            @Param("politicianId") Long politicianId,
            @Param("category") String category,
            @Param("supplier") String supplier,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount
    );
}