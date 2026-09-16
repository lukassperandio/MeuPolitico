package com.meupolitico.service;

import com.meupolitico.dto.response.RankingItemResponse;
import com.meupolitico.entity.Politician;
import com.meupolitico.repository.AssetRepository;
import com.meupolitico.repository.AttendanceRepository;
import com.meupolitico.repository.ExpenseRepository;
import com.meupolitico.repository.PoliticianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class RankingService {

    private final PoliticianRepository politicianRepository;
    private final ExpenseRepository expenseRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssetRepository assetRepository;

    private static final Logger log = LoggerFactory.getLogger(RankingService.class);

    public RankingService(PoliticianRepository politicianRepository,
                          ExpenseRepository expenseRepository,
                          AttendanceRepository attendanceRepository,
                          AssetRepository assetRepository) {
        this.politicianRepository = politicianRepository;
        this.expenseRepository = expenseRepository;
        this.attendanceRepository = attendanceRepository;
        this.assetRepository = assetRepository;
    }


    public List<RankingItemResponse> rankByExpenses(String state,
                                                    String party,
                                                    String position,
                                                    String name,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    String order) {
        List<Object[]> rows = expenseRepository.findTopExpenseTotals();
        List<RankingItemResponse> ranking = new ArrayList<>();

        for (Object[] row : rows) {
            Long politicianId = ((Number) row[0]).longValue();
            BigDecimal total = row[1] != null
                    ? new BigDecimal(row[1].toString())
                    : BigDecimal.ZERO;

            Politician politician = politicianRepository.findById(politicianId).orElse(null);
            if (politician == null) {
                continue;
            }
            if (!matchesFilters(politician, state, party, position, name)) {
                continue;
            }

            ranking.add(new RankingItemResponse(
                    0,
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    total,
                    null
            ));
        }

        return finalizeRanking(ranking, order);
    }

    public List<RankingItemResponse> rankByAttendance(String state,
                                                      String party,
                                                      String position,
                                                      String name,
                                                      LocalDate startDate,
                                                      LocalDate endDate,
                                                      String order) {

        long t0 = System.currentTimeMillis();

        List<Object[]> presentRows =
                attendanceRepository.countPresentGroupedByPoliticianBetween(startDate, endDate);

        long t1 = System.currentTimeMillis();

        Map<Long, Long> presentByPolitician = new HashMap<>();
        for (Object[] row : presentRows) {
            presentByPolitician.put(
                    ((Number) row[0]).longValue(),
                    ((Number) row[1]).longValue());
        }

        Map<Long, Politician> politiciansById = politicianRepository.findAll().stream()
                .collect(Collectors.toMap(Politician::getId, p -> p));

        long t2 = System.currentTimeMillis();

        // MUDANÇA: em vez de uma query por data distinta (96 queries × 85ms),
        // carrega-se a lista completa de eventos distintos UMA vez (~500-1000 linhas)
        // e conta-se em memória. Instantâneo.
        List<Object[]> allEvents = attendanceRepository.findDistinctEvents();

        long t3 = System.currentTimeMillis();

        List<RankingItemResponse> items = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : presentByPolitician.entrySet()) {
            Long politicianId = entry.getKey();
            long present = entry.getValue();

            Politician p = politiciansById.get(politicianId);
            if (p == null) continue;
            if (!matchesFilters(p, state, party, position, name)) continue;

            LocalDate since = p.getMandateStart();
            if (startDate != null && (since == null || startDate.isAfter(since))) {
                since = startDate;
            }

            long total = countEventsInRange(allEvents, since, endDate);
            if (total <= 0) continue;

            BigDecimal rate = BigDecimal.valueOf(present * 100.0 / total)
                    .setScale(2, RoundingMode.HALF_UP);

            items.add(new RankingItemResponse(
                    0,
                    p.getId(),
                    p.getName(),
                    p.getParty(),
                    p.getState(),
                    p.getPosition(),
                    rate,
                    (double) present
            ));
        }

        long t4 = System.currentTimeMillis();

        log.info("rankByAttendance: presents={}ms, politicians={}ms, events={}ms, loop={}ms",
                t1 - t0, t2 - t1, t3 - t2, t4 - t3);

        return finalizeRanking(items, order);
    }

    private long countEventsInRange(List<Object[]> events, LocalDate since, LocalDate end) {
        long count = 0;
        for (Object[] e : events) {
            LocalDate d = toLocalDate(e[0]);
            if (d == null) continue;
            if (since != null && d.isBefore(since)) continue;
            if (end != null && d.isAfter(end)) continue;
            count++;
        }
        return count;
    }

    private LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDate ld) return ld;
        if (o instanceof java.sql.Date sd) return sd.toLocalDate();
        if (o instanceof java.sql.Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        throw new IllegalStateException(
                "Unexpected date type from findDistinctEvents: " + o.getClass().getName());
    }

    public List<RankingItemResponse> rankByAssets(String state,
                                                  String party,
                                                  String position,
                                                  String name,
                                                  Integer year,
                                                  String order) {
        List<Object[]> rows = assetRepository.findLatestAssetsByPolitician();
        List<RankingItemResponse> ranking = new ArrayList<>();

        for (Object[] row : rows) {
            Long politicianId = ((Number) row[0]).longValue();
            BigDecimal value = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            Integer assetYear = row[2] != null ? ((Number) row[2]).intValue() : null;

            if (year != null && assetYear != null && !assetYear.equals(year)) {
                continue;
            }
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            Politician politician = politicianRepository.findById(politicianId).orElse(null);
            if (politician == null || !matchesFilters(politician, state, party, position, name)) {
                continue;
            }

            ranking.add(new RankingItemResponse(
                    0,
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    value,
                    assetYear != null ? assetYear.doubleValue() : null
            ));
        }

        return finalizeRanking(ranking, order);
    }

    private boolean matchesFilters(Politician politician,
                                   String state,
                                   String party,
                                   String position,
                                   String name) {
        if (state != null && !state.isBlank()
                && (politician.getState() == null || !politician.getState().equalsIgnoreCase(state.trim()))) {
            return false;
        }
        if (party != null && !party.isBlank()
                && (politician.getParty() == null
                || !politician.getParty().equalsIgnoreCase(party.trim()))) {
            return false;
        }
        if (position != null && !position.isBlank()
                && (politician.getPosition() == null
                || !politician.getPosition().toLowerCase().contains(position.trim().toLowerCase()))) {
            return false;
        }
        if (name != null && !name.isBlank()
                && (politician.getName() == null
                || !politician.getName().toLowerCase().contains(name.trim().toLowerCase()))) {
            return false;
        }
        return true;
    }

    private List<RankingItemResponse> finalizeRanking(List<RankingItemResponse> ranking, String order) {
        if ("asc".equalsIgnoreCase(order)) {
            ranking.sort(Comparator.comparing(RankingItemResponse::value));
        } else {
            ranking.sort(Comparator.comparing(RankingItemResponse::value).reversed());
        }

        List<RankingItemResponse> result = new ArrayList<>();
        int pos = 1;
        int limit = ranking.size();
        for (int i = 0; i < limit; i++) {
            RankingItemResponse item = ranking.get(i);
            result.add(new RankingItemResponse(
                    pos++,
                    item.politicianId(),
                    item.politicianName(),
                    item.party(),
                    item.state(),
                    item.positionTitle(),
                    item.value(),
                    item.secondaryValue()
            ));
        }
        return result;
    }
}