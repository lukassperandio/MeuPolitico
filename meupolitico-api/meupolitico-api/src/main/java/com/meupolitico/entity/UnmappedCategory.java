package com.meupolitico.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registra textos brutos de categoria de despesa que apareceram numa importação
 * mas ainda não têm um ExpenseCategoryMapping cadastrado.
 *
 * Serve como fila de revisão: em vez de a despesa "sumir" silenciosamente
 * dentro de OTHER, fica registrado aqui pra alguém decidir o mapeamento certo.
 */
@Entity
@Table(
        name = "unmapped_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"state", "raw_category_label"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnmappedCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "raw_category_label", nullable = false, length = 300)
    private String rawCategoryLabel;

    @Column(nullable = false)
    private Long occurrences = 1L;

    @Column(name = "first_seen_at", updatable = false)
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.firstSeenAt = now;
        this.lastSeenAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastSeenAt = LocalDateTime.now();
    }
}