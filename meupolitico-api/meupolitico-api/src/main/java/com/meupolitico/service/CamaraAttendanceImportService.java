package com.meupolitico.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meupolitico.entity.Attendance;
import com.meupolitico.entity.Politician;
import com.meupolitico.enums.AttendanceStatus;
import com.meupolitico.integration.camara.dto.CamaraAttendanceItem;
import com.meupolitico.repository.AttendanceRepository;
import com.meupolitico.repository.PoliticianRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
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

@Service
public class CamaraAttendanceImportService {

    private static final Logger log = LoggerFactory.getLogger(CamaraAttendanceImportService.class);
    private static final int BATCH_SIZE = 500;

    private final PoliticianRepository politicianRepository;
    private final AttendanceRepository attendanceRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${camara.attendance.base-url}")
    private String attendanceBaseUrl;

    public CamaraAttendanceImportService(PoliticianRepository politicianRepository,
                                         AttendanceRepository attendanceRepository) {
        this.politicianRepository = politicianRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public int importYear(int year) {
        String url = attendanceBaseUrl + "/eventosPresencaDeputados-" + year + ".json";
        log.info("Downloading attendance file: {}", url);

        Map<String, Politician> politiciansByExternalId = new HashMap<>();
        for (Politician p : politicianRepository.findAll()) {
            if (p.getExternalId() != null) {
                politiciansByExternalId.put(p.getExternalId(), p);
            }
        }

        Set<String> existingIds = new HashSet<>(attendanceRepository.findAllExternalIds());
        log.info("Politicians={}, existing attendances={}", politiciansByExternalId.size(), existingIds.size());

        Path tempFile = null;
        int imported = 0;
        int skippedNoPolitician = 0;
        int skippedDuplicate = 0;
        List<Attendance> batch = new ArrayList<>(BATCH_SIZE);

        try {
            tempFile = Files.createTempFile("attendance-" + year + "-", ".json");
            try (InputStream in = URI.create(url).toURL().openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Download finished: {} bytes", Files.size(tempFile));

            try (InputStream fileStream = Files.newInputStream(tempFile);
                 JsonParser parser = objectMapper.getFactory().createParser(fileStream)) {

                while (parser.nextToken() != null) {
                    if (parser.currentToken() == JsonToken.FIELD_NAME
                            && "dados".equals(parser.currentName())) {
                        parser.nextToken(); // START_ARRAY

                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            CamaraAttendanceItem item =
                                    objectMapper.readValue(parser, CamaraAttendanceItem.class);

                            if (item.idDeputado() == null || item.idEvento() == null) {
                                skippedNoPolitician++;
                                continue;
                            }

                            String externalId = "camara-evt-" + item.idEvento()
                                    + "-dep-" + item.idDeputado();

                            if (existingIds.contains(externalId)) {
                                skippedDuplicate++;
                                continue;
                            }

                            Politician politician = politiciansByExternalId.get(
                                    String.valueOf(item.idDeputado()));
                            if (politician == null) {
                                skippedNoPolitician++;
                                continue;
                            }

                            Attendance attendance = new Attendance();
                            attendance.setPolitician(politician);
                            attendance.setExternalId(externalId);
                            attendance.setDate(parseDate(item.dataHoraInicio()));
                            attendance.setStatus(AttendanceStatus.PRESENT);
                            attendance.setSessionType("Evento Câmara");
                            attendance.setSource("Câmara dos Deputados");

                            batch.add(attendance);
                            existingIds.add(externalId);
                            imported++;

                            if (batch.size() >= BATCH_SIZE) {
                                saveBatch(batch);
                                batch.clear();
                            }

                            if ((imported + skippedDuplicate + skippedNoPolitician) % 5000 == 0) {
                                log.info("Progress: imported={}, dup={}, noPol={}",
                                        imported, skippedDuplicate, skippedNoPolitician);
                            }
                        }
                    }
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch);
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to import attendance year " + year + ": " + e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignored) {
                }
            }
        }

        log.info("Attendance {} done. imported={}, dup={}, noPol={}",
                year, imported, skippedDuplicate, skippedNoPolitician);
        return imported;
    }

    @Transactional
    protected void saveBatch(List<Attendance> batch) {
        attendanceRepository.saveAll(batch);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }
        // "2024-03-26T10:13:03"
        return LocalDateTime.parse(value).toLocalDate();
    }
}