package com.meupolitico.repository;

import com.meupolitico.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByPoliticianId(Long politicianId);

    List<Expense> findByCategoryContainingIgnoreCase(String category);

    List<Expense> findBySupplierContainingIgnoreCase(String supplier);

    List<Expense> findByDate(LocalDate date);

    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByAmountGreaterThanEqual(BigDecimal amount);
}