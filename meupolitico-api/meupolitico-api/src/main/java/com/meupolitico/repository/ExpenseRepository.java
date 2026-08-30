package com.meupolitico.repository;

import com.meupolitico.entity.Expense;
import com.meupolitico.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
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
}