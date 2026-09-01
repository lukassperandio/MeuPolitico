package com.meupolitico.service;

import com.meupolitico.entity.Expense;
import com.meupolitico.entity.ExpenseCategoryMapping;
import com.meupolitico.enums.ExpenseCategory;
import com.meupolitico.repository.ExpenseCategoryMappingRepository;
import com.meupolitico.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseRecategorizeService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseRecategorizeService.class);
    private static final int BATCH_SIZE = 500;

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryMappingRepository mappingRepository;

    public ExpenseRecategorizeService(ExpenseRepository expenseRepository,
                                      ExpenseCategoryMappingRepository mappingRepository) {
        this.expenseRepository = expenseRepository;
        this.mappingRepository = mappingRepository;
    }

    @Transactional
    public int recategorizeAll() {
        Map<String, ExpenseCategory> mappings = loadMappings();
        log.info("Loaded {} category mappings", mappings.size());

        List<Expense> expenses = expenseRepository.findAll();
        int updated = 0;
        int checked = 0;

        for (Expense expense : expenses) {
            checked++;

            String state = expense.getPolitician() != null && expense.getPolitician().getState() != null
                    ? expense.getPolitician().getState()
                    : "";

            ExpenseCategory resolved = resolveCategoryInMemory(mappings, state, expense.getDescription());

            if (resolved != null && resolved != expense.getCategory()) {
                expense.setCategory(resolved);
                updated++;
            }

            if (checked % BATCH_SIZE == 0) {
                expenseRepository.saveAll(expenses.subList(Math.max(0, checked - BATCH_SIZE), checked));
                log.info("Progress: checked={}, updated={}", checked, updated);
            }
        }

        expenseRepository.saveAll(expenses);
        log.info("Recategorize done. checked={}, updated={}", checked, updated);
        return updated;
    }

    private Map<String, ExpenseCategory> loadMappings() {
        Map<String, ExpenseCategory> map = new HashMap<>();
        for (ExpenseCategoryMapping mapping : mappingRepository.findAll()) {
            map.put(normalizeKey(mapping.getState(), mapping.getRawCategoryLabel()), mapping.getMappedCategory());
        }
        return map;
    }

    private ExpenseCategory resolveCategoryInMemory(Map<String, ExpenseCategory> mappings,
                                                    String state,
                                                    String rawLabel) {
        if (rawLabel == null || rawLabel.isBlank()) {
            return ExpenseCategory.OTHER;
        }

        String normalizedLabel = rawLabel.trim().toUpperCase().replaceAll("\\s+", " ");

        ExpenseCategory mapped = mappings.get(normalizeKey(state, normalizedLabel));
        if (mapped != null) {
            return mapped;
        }

        ExpenseCategory byLabel = mappings.get(normalizeKey("", normalizedLabel));
        if (byLabel != null) {
            return byLabel;
        }

        for (Map.Entry<String, ExpenseCategory> entry : mappings.entrySet()) {
            if (entry.getKey().endsWith("|" + normalizedLabel)) {
                return entry.getValue();
            }
        }

        return guessFromLabel(normalizedLabel);
    }

    private ExpenseCategory guessFromLabel(String label) {
        if (label.contains("COMBUSTÍVEL") || label.contains("COMBUSTIVEL") || label.contains("LUBRIFICANTE")) {
            return ExpenseCategory.FUEL;
        }
        if (label.contains("PASSAGEM AÉREA") || label.contains("PASSAGEM AEREA") || label.contains("PASSAGEM")) {
            return ExpenseCategory.AIRFARE;
        }
        if (label.contains("ESCRITÓRIO") || label.contains("ESCRITORIO")) {
            return ExpenseCategory.OFFICE_MAINTENANCE;
        }
        if (label.contains("DIVULGAÇÃO") || label.contains("DIVULGACAO") || label.contains("PUBLICIDADE")) {
            return ExpenseCategory.ADVERTISING;
        }
        if (label.contains("TELEFON") || label.contains("INTERNET")) {
            return ExpenseCategory.PHONE_INTERNET;
        }
        if (label.contains("CORREIO") || label.contains("POSTAIS")) {
            return ExpenseCategory.MAIL;
        }
        if (label.contains("ALIMENTA")) {
            return ExpenseCategory.MEALS;
        }
        if (label.contains("HOSPEDAG")) {
            return ExpenseCategory.LODGING;
        }
        if (label.contains("CONSULTOR") || label.contains("PESQUISA") || label.contains("TRABALHOS TÉCNICOS")) {
            return ExpenseCategory.CONSULTING;
        }
        if (label.contains("LOCAÇÃO") || label.contains("LOCACAO") || label.contains("FRETAMENTO")) {
            return ExpenseCategory.VEHICLE_RENTAL;
        }
        if (label.contains("SEGURAN")) {
            return ExpenseCategory.SECURITY;
        }

        if (label.contains("TÁXI") || label.contains("TAXI")
                || label.contains("PEDÁGIO") || label.contains("PEDAGIO")
                || label.contains("ESTACIONAMENTO")
                || label.contains("PASSAGENS TERRESTRES")
                || label.contains("MARÍTIMAS") || label.contains("MARITIMAS")
                || label.contains("FLUVIAIS")) {
            return ExpenseCategory.GROUND_TRANSPORT;
        }
        if (label.contains("ASSINATURA DE PUBLICAÇÕES") || label.contains("PUBLICAÇÕES")
                || label.contains("TOKEN") || label.contains("CERTIFICADO DIGITAL")) {
            return ExpenseCategory.OFFICE_SUPPLIES;
        }

        return ExpenseCategory.OTHER;
    }

    private String normalizeKey(String state, String label) {
        String normalizedState = state == null ? "" : state.trim().toUpperCase();
        String normalizedLabel = label == null ? "" : label.trim().toUpperCase().replaceAll("\\s+", " ");
        return normalizedState + "|" + normalizedLabel;
    }
}