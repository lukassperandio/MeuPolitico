package com.meupolitico.entity;

import com.meupolitico.enums.ExpenseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mapeia o texto bruto de categoria de despesa, como recebido da API/planilha
 * de cada Assembleia Legislativa estadual, para a categoria interna
 * (ExpenseCategory) usada pela aplicação.
 *
 * Exemplo: state = "SP", rawCategoryLabel = "COMBUSTÍVEIS E LUBRIFICANTES"
 *          -> mappedCategory = ExpenseCategory.FUEL
 */
@Entity
@Table(
        name = "expense_category_mapping",
        uniqueConstraints = @UniqueConstraint(columnNames = {"state", "raw_category_label"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "raw_category_label", nullable = false, length = 300)
    private String rawCategoryLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "mapped_category", nullable = false, length = 50)
    private ExpenseCategory mappedCategory;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}