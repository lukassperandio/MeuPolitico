package com.meupolitico.service;

import com.meupolitico.entity.ExpenseCategoryMapping;
import com.meupolitico.entity.UnmappedCategory;
import com.meupolitico.enums.ExpenseCategory;
import com.meupolitico.repository.ExpenseCategoryMappingRepository;
import com.meupolitico.repository.UnmappedCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryResolverServiceTest {

    @Mock
    private ExpenseCategoryMappingRepository mappingRepository;

    @Mock
    private UnmappedCategoryRepository unmappedCategoryRepository;

    private CategoryResolverService resolverService;

    @BeforeEach
    void setUp() {
        resolverService = new CategoryResolverService(mappingRepository, unmappedCategoryRepository);
    }

    @Test
    void deveResolverCategoriaQuandoMapeamentoExiste() {
        ExpenseCategoryMapping mapping = new ExpenseCategoryMapping(
                1L, "SP", "COMBUSTÍVEIS E LUBRIFICANTES", ExpenseCategory.FUEL, null);

        when(mappingRepository.findByStateAndRawCategoryLabel("SP", "COMBUSTÍVEIS E LUBRIFICANTES"))
                .thenReturn(Optional.of(mapping));

        ExpenseCategory result = resolverService.resolveCategory("SP", "Combustíveis e Lubrificantes");

        assertThat(result).isEqualTo(ExpenseCategory.FUEL);
        verify(unmappedCategoryRepository, never()).save(any());
    }

    @Test
    void deveNormalizarEstadoELabelAntesDeBuscar() {
        // texto vindo "sujo": minúsculo, espaço extra e espaço nas pontas
        when(mappingRepository.findByStateAndRawCategoryLabel("RJ", "AUXÍLIO MORADIA"))
                .thenReturn(Optional.of(new ExpenseCategoryMapping(
                        2L, "RJ", "AUXÍLIO MORADIA", ExpenseCategory.HOUSING_ALLOWANCE, null)));

        ExpenseCategory result = resolverService.resolveCategory("  rj ", "  auxílio   moradia  ");

        assertThat(result).isEqualTo(ExpenseCategory.HOUSING_ALLOWANCE);
        verify(mappingRepository).findByStateAndRawCategoryLabel("RJ", "AUXÍLIO MORADIA");
    }

    @Test
    void deveRetornarOtherERegistrarNaoMapeadoQuandoNaoExisteMapeamento() {
        when(mappingRepository.findByStateAndRawCategoryLabel(eq("AM"), any()))
                .thenReturn(Optional.empty());
        when(unmappedCategoryRepository.findByStateAndRawCategoryLabel(eq("AM"), any()))
                .thenReturn(Optional.empty());

        ExpenseCategory result = resolverService.resolveCategory("AM", "COMBUSTÍVEL PARA EMBARCAÇÃO OFICIAL");

        assertThat(result).isEqualTo(ExpenseCategory.OTHER);

        ArgumentCaptor<UnmappedCategory> captor = ArgumentCaptor.forClass(UnmappedCategory.class);
        verify(unmappedCategoryRepository).save(captor.capture());

        UnmappedCategory saved = captor.getValue();
        assertThat(saved.getState()).isEqualTo("AM");
        assertThat(saved.getRawCategoryLabel()).isEqualTo("COMBUSTÍVEL PARA EMBARCAÇÃO OFICIAL");
        assertThat(saved.getOccurrences()).isEqualTo(1L);
    }

    @Test
    void deveIncrementarOccurrencesQuandoTextoNaoMapeadoJaExiste() {
        UnmappedCategory existing = new UnmappedCategory(
                10L, "MG", "DESPESA NAO CATALOGADA", 3L, null, null);

        when(mappingRepository.findByStateAndRawCategoryLabel(eq("MG"), any()))
                .thenReturn(Optional.empty());
        when(unmappedCategoryRepository.findByStateAndRawCategoryLabel(eq("MG"), any()))
                .thenReturn(Optional.of(existing));

        ExpenseCategory result = resolverService.resolveCategory("MG", "Despesa não catalogada");

        assertThat(result).isEqualTo(ExpenseCategory.OTHER);

        ArgumentCaptor<UnmappedCategory> captor = ArgumentCaptor.forClass(UnmappedCategory.class);
        verify(unmappedCategoryRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getOccurrences()).isEqualTo(4L);
    }

    @Test
    void deveRetornarOtherSemConsultarRepositoriosQuandoLabelForNuloOuVazio() {
        assertThat(resolverService.resolveCategory("SP", null)).isEqualTo(ExpenseCategory.OTHER);
        assertThat(resolverService.resolveCategory("SP", "   ")).isEqualTo(ExpenseCategory.OTHER);

        verify(mappingRepository, never()).findByStateAndRawCategoryLabel(any(), any());
        verify(unmappedCategoryRepository, never()).save(any());
    }
}