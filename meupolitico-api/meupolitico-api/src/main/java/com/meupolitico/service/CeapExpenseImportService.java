package com.meupolitico.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meupolitico.entity.Expense;
import com.meupolitico.entity.ExpenseCategoryMapping;
import com.meupolitico.entity.Politician;
import com.meupolitico.enums.ExpenseCategory;
import com.meupolitico.integration.camara.dto.CeapExpenseItem;
import com.meupolitico.repository.ExpenseCategoryMappingRepository;
import com.meupolitico.repository.ExpenseRepository;
import com.meupolitico.repository.PoliticianRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

@Service
public class CeapExpenseImportService {

    private static final Logger log = LoggerFactory.getLogger(CeapExpenseImportService.class);
    private static final int BATCH_SIZE = 500;

    private final PoliticianRepository politicianRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryMappingRepository mappingRepository;

    @Value("${camara.ceap.base-url}")
    private String ceapBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CeapExpenseImportService(PoliticianRepository politicianRepository,
                                    ExpenseRepository expenseRepository,
                                    ExpenseCategoryMappingRepository mappingRepository) {
        this.politicianRepository = politicianRepository;
        this.expenseRepository = expenseRepository;
        this.mappingRepository = mappingRepository;
    }
    public int importYear(int year) {
        String url = ceapBaseUrl + "/Ano-" + year + ".json.zip";
        log.info("Downloading CEAP file: {}", url);

        Map<String, Politician> politiciansByExternalId = new HashMap<>();
        for (Politician politician : politicianRepository.findAll()) {
            if (politician.getExternalId() != null) {
                politiciansByExternalId.put(politician.getExternalId(), politician);
            }
        }
        log.info("Politicians loaded in memory: {}", politiciansByExternalId.size());

        Set<String> existingExpenseExternalIds = new HashSet<>(expenseRepository.findAllExternalIds());
        log.info("Existing expenses loaded: {}", existingExpenseExternalIds.size());

        Map<String, ExpenseCategory> categoryByStateAndLabel = new HashMap<>();
        for (ExpenseCategoryMapping mapping : mappingRepository.findAll()) {
            String key = normalizeKey(mapping.getState(), mapping.getRawCategoryLabel());
            categoryByStateAndLabel.put(key, mapping.getMappedCategory());
        }
        log.info("Category mappings loaded: {}", categoryByStateAndLabel.size());

        Path tempZip = null;
        int imported = 0;
        int skippedNoPolitician = 0;
        int skippedDuplicate = 0;

        List<Expense> batch = new ArrayList<>(BATCH_SIZE);

        try {
            tempZip = Files.createTempFile("ceap-" + year + "-", ".json.zip");
            try (InputStream in = URI.create(url).toURL().openStream()) {
                Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Download finished: {} ({} bytes)", tempZip, Files.size(tempZip));

            try (InputStream fileStream = Files.newInputStream(tempZip);
                 ZipInputStream zipStream = new ZipInputStream(fileStream)) {

                if (zipStream.getNextEntry() == null) {
                    throw new IllegalStateException("ZIP without files: " + url);
                }

                try (JsonParser parser = objectMapper.getFactory().createParser(zipStream)) {
                    while (parser.nextToken() != null) {
                        if (parser.currentToken() == JsonToken.FIELD_NAME
                                && "dados".equals(parser.currentName())) {
                            parser.nextToken();

                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                CeapExpenseItem item = objectMapper.readValue(parser, CeapExpenseItem.class);

                                Integer deputyId = item.idDeputado() != null
                                        ? item.idDeputado()
                                        : item.numeroDeputadoID();

                                if (deputyId == null || item.idDocumento() == null) {
                                    skippedNoPolitician++;
                                    continue;
                                }

                                String expenseExternalId = "camara-" + item.idDocumento();
                                if (existingExpenseExternalIds.contains(expenseExternalId)) {
                                    skippedDuplicate++;
                                    continue;
                                }

                                Politician politician = politiciansByExternalId.get(String.valueOf(deputyId));
                                if (politician == null) {
                                    skippedNoPolitician++;
                                    continue;
                                }

                                if (politician == null) {
                                    skippedNoPolitician++;
                                    continue;
                                }

                                String state = politician.getState() != null ? politician.getState() : "";
                                ExpenseCategory category = resolveCategoryInMemory(
                                        categoryByStateAndLabel, state, item.descricao());

                                Expense expense = new Expense();
                                expense.setPolitician(politician);
                                expense.setExternalId(expenseExternalId);
                                expense.setAmount(parseAmount(item.valorLiquido()));
                                expense.setDate(parseDate(item.dataEmissao()));
                                expense.setCategory(category);
                                expense.setSupplier(safe(item.fornecedor()));
                                expense.setDocumentNumber(safe(item.cnpjCPF()));
                                expense.setDescription(safe(item.descricao()));
                                expense.setSource("Câmara dos Deputados");

                                batch.add(expense);
                                existingExpenseExternalIds.add(expenseExternalId);
                                imported++;

                                if (batch.size() >= BATCH_SIZE) {
                                    saveBatch(batch);
                                    batch.clear();
                                }

                                if ((imported + skippedDuplicate + skippedNoPolitician) % 5000 == 0) {
                                    log.info("Progress: imported={}, duplicates={}, noPolitician={}",
                                            imported, skippedDuplicate, skippedNoPolitician);
                                }
                            }
                        }
                    }
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch);
                batch.clear();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to import CEAP year " + year + ": " + e.getMessage(), e);
        } finally {
            if (tempZip != null) {
                try {
                    Files.deleteIfExists(tempZip);
                } catch (Exception ignored) {
                }
            }
        }

        log.info("CEAP {} done. imported={}, duplicates={}, noPolitician={}",
                year, imported, skippedDuplicate, skippedNoPolitician);
        return imported;
    }

    @Transactional
    protected void saveBatch(List<Expense> batch) {
        expenseRepository.saveAll(batch);
    }

    private ExpenseCategory resolveCategoryInMemory(Map<String, ExpenseCategory> mappings,
                                                    String state,
                                                    String rawLabel) {
        if (rawLabel == null || rawLabel.isBlank()) {
            return ExpenseCategory.OTHER;
        }

        String normalizedLabel = rawLabel.trim().toUpperCase().replaceAll("\\s+", " ");

        String keyWithState = normalizeKey(state, normalizedLabel);
        ExpenseCategory mapped = mappings.get(keyWithState);
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

        return ExpenseCategory.OTHER;
    }

    private String normalizeKey(String state, String label) {
        String normalizedState = state == null ? "" : state.trim().toUpperCase();
        String normalizedLabel = label == null ? "" : label.trim().toUpperCase().replaceAll("\\s+", " ");
        return normalizedState + "|" + normalizedLabel;
    }

    private BigDecimal parseAmount(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim().replace(",", ""));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.of(2024, 1, 1);
        }
        return LocalDateTime.parse(value).toLocalDate();
    }

    private String safe(String value) {
        return value == null ? null : value.trim();
    }
}