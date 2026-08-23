package com.meupolitico.mapper;

import com.meupolitico.dto.request.ExpenseRequest;
import com.meupolitico.dto.response.ExpenseResponse;
import com.meupolitico.entity.Expense;
import com.meupolitico.entity.Politician;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseRequest request, Politician politician) {
        Expense expense = new Expense();

        expense.setPolitician(politician);
        expense.setAmount(request.amount());
        expense.setDate(request.date());
        expense.setCategory(request.category());
        expense.setSupplier(request.supplier());
        expense.setDocumentNumber(request.documentNumber());
        expense.setDescription(request.description());
        expense.setSource(request.source());

        return expense;
    }

    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getPolitician().getId(),
                expense.getPolitician().getName(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory(),
                expense.getSupplier(),
                expense.getDocumentNumber(),
                expense.getDescription(),
                expense.getSource(),
                expense.getCreatedAt()
        );
    }
}