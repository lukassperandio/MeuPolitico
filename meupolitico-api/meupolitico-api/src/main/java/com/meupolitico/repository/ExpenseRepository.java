package com.meupolitico.repository;

import com.meupolitico.entity.Expense;
import com.meupolitico.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    SELECT e.politician_id AS politicianId,
           SUM(e.amount) AS total
    FROM expense e
    GROUP BY e.politician_id
    ORDER BY total DESC
    LIMIT 1000
    """, nativeQuery = true)
    List<Object[]> findTopExpenseTotals();

    @Query(value = """
    SELECT COALESCE(SUM(e.amount), 0), COUNT(*)
    FROM expense e
    WHERE e.politician_id = :politicianId
    """, nativeQuery = true)
    Object[] sumAndCountByPoliticianId(@Param("politicianId") Long politicianId);

    @Query(value = """
    SELECT TO_CHAR(e.date, 'YYYY-MM') AS month,
           COALESCE(SUM(e.amount), 0) AS total
    FROM expense e
    WHERE e.politician_id = :politicianId
    GROUP BY TO_CHAR(e.date, 'YYYY-MM')
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> sumByMonth(@Param("politicianId") Long politicianId);

    @Query("select max(e.date) from Expense e where e.politician.id = :id")
    Optional<LocalDate> findLastDate(@Param("id") Long id);
}