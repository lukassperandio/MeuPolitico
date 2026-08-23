package com.meupolitico.service;

import com.meupolitico.dto.request.ExpenseRequest;
import com.meupolitico.dto.response.ExpenseResponse;
import com.meupolitico.entity.Expense;
import com.meupolitico.entity.Politician;
import com.meupolitico.exception.ResourceNotFoundException;
import com.meupolitico.mapper.ExpenseMapper;
import com.meupolitico.repository.ExpenseRepository;
import com.meupolitico.repository.PoliticianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PoliticianRepository politicianRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseService(ExpenseRepository expenseRepository,
                          PoliticianRepository politicianRepository,
                          ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.politicianRepository = politicianRepository;
        this.expenseMapper = expenseMapper;
    }

    public List<ExpenseResponse> findAll() {
        return expenseRepository.findAll()
                .stream()
                .map(expenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ExpenseResponse findById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        return expenseMapper.toResponse(expense);
    }

    public List<ExpenseResponse> findByPoliticianId(Long politicianId) {
        return expenseRepository.findByPoliticianId(politicianId)
                .stream()
                .map(expenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ExpenseResponse create(ExpenseRequest request) {
        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        Expense expense = expenseMapper.toEntity(request, politician);
        Expense savedExpense = expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);
    }

    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        Politician politician = politicianRepository.findById(request.politicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + request.politicianId()));

        expense.setPolitician(politician);
        expense.setAmount(request.amount());
        expense.setDate(request.date());
        expense.setCategory(request.category());
        expense.setSupplier(request.supplier());
        expense.setDocumentNumber(request.documentNumber());
        expense.setDescription(request.description());
        expense.setSource(request.source());

        Expense updatedExpense = expenseRepository.save(expense);

        return expenseMapper.toResponse(updatedExpense);
    }

    public void delete(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }

        expenseRepository.deleteById(id);
    }
}