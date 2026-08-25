package com.meupolitico.service;

import com.meupolitico.entity.ExpenseCategoryMapping;
import com.meupolitico.entity.UnmappedCategory;
import com.meupolitico.enums.ExpenseCategory;
import com.meupolitico.repository.ExpenseCategoryMappingRepository;
import com.meupolitico.repository.UnmappedCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Resolve o texto bruto de categoria de despesa (vindo da API/planilha de cada
 * Assembleia estadual) para a ExpenseCategory interna da aplicação.
 *
 * Fluxo:
 *  1. Normaliza o texto (maiúsculo, sem espaços nas pontas).
 *  2. Procura um mapeamento cadastrado para (state, rawLabel).
 *  3. Se encontrar, devolve a categoria mapeada.
 *  4. Se não encontrar, registra/atualiza em UnmappedCategory e devolve OTHER
 *     como fallback, sem lançar exceção e sem interromper a importação.
 */
@Service
public class CategoryResolverService {

    private static final Logger log = LoggerFactory.getLogger(CategoryResolverService.class);

    private final ExpenseCategoryMappingRepository mappingRepository;
    private final UnmappedCategoryRepository unmappedCategoryRepository;

    public CategoryResolverService(ExpenseCategoryMappingRepository mappingRepository,
                                   UnmappedCategoryRepository unmappedCategoryRepository) {
        this.mappingRepository = mappingRepository;
        this.unmappedCategoryRepository = unmappedCategoryRepository;
    }

    public ExpenseCategory resolveCategory(String state, String rawLabel) {
        if (rawLabel == null || rawLabel.isBlank()) {
            return ExpenseCategory.OTHER;
        }

        String normalizedState = normalizeState(state);
        String normalizedLabel = normalizeLabel(rawLabel);

        return mappingRepository.findByStateAndRawCategoryLabel(normalizedState, normalizedLabel)
                .map(ExpenseCategoryMapping::getMappedCategory)
                .orElseGet(() -> {
                    registerUnmapped(normalizedState, normalizedLabel);
                    return ExpenseCategory.OTHER;
                });
    }

    private void registerUnmapped(String state, String rawLabel) {
        log.warn("Categoria de despesa não mapeada: state={}, rawLabel='{}'", state, rawLabel);

        unmappedCategoryRepository.findByStateAndRawCategoryLabel(state, rawLabel)
                .ifPresentOrElse(
                        existing -> {
                            existing.setOccurrences(existing.getOccurrences() + 1);
                            unmappedCategoryRepository.save(existing);
                        },
                        () -> unmappedCategoryRepository.save(
                                new UnmappedCategory(null, state, rawLabel, 1L, null, null))
                );
    }

    private String normalizeState(String state) {
        return state == null ? "" : state.trim().toUpperCase();
    }

    private String normalizeLabel(String label) {
        return label.trim().toUpperCase().replaceAll("\\s+", " ");
    }
}