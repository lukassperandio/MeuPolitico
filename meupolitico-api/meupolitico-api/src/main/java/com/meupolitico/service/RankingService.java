package com.meupolitico.service;

import com.meupolitico.dto.response.RankingItemResponse;
import com.meupolitico.entity.Asset;
import com.meupolitico.entity.Attendance;
import com.meupolitico.entity.Expense;
import com.meupolitico.entity.Politician;
import com.meupolitico.enums.AttendanceStatus;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RankingService {

    private final PoliticianRepository politicianRepository;
    private final ExpenseRepository expenseRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssetRepository assetRepository;

    private static final int TOP_LIMIT = Integer.MAX_VALUE;

    public RankingService(PoliticianRepository politicianRepository,
                          ExpenseRepository expenseRepository,
                          AttendanceRepository attendanceRepository,
                          AssetRepository assetRepository) {
        this.politicianRepository = politicianRepository;
        this.expenseRepository = expenseRepository;
        this.attendanceRepository = attendanceRepository;
        this.assetRepository = assetRepository;
    }

    private boolean matchesFilters(Politician politician, String state, String party, String position) {
        if (state != null && !state.isBlank()
                && (politician.getState() == null || !politician.getState().equalsIgnoreCase(state))) {
            return false;
        }
        if (party != null && !party.isBlank()
                && (politician.getParty() == null || !politician.getParty().equalsIgnoreCase(party))) {
            return false;
        }
        if (position != null && !position.isBlank()
                && (politician.getPosition() == null
                || !politician.getPosition().toLowerCase().contains(position.toLowerCase()))) {
            return false;
        }
        return true;
    }

    private List<Politician> filterPoliticians(String state, String party, String position) {
        return politicianRepository.findAll().stream()
                .filter(p -> state == null || state.isBlank()
                        || (p.getState() != null && p.getState().equalsIgnoreCase(state)))
                .filter(p -> party == null || party.isBlank()
                        || (p.getParty() != null && p.getParty().equalsIgnoreCase(party)))
                .filter(p -> position == null || position.isBlank()
                        || (p.getPosition() != null && p.getPosition().toLowerCase()
                        .contains(position.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<RankingItemResponse> rankByExpenses(String state,
                                                    String party,
                                                    String position,
                                                    String name,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    String order) {
        // startDate/endDate: ainda não entram no SQL de findTopExpenseTotals
        // (filtro de período nos gastos = evolução futura da query)
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
        long totalSessions;
        if (startDate != null || endDate != null) {
            totalSessions = attendanceRepository.countDistinctEventsBetween(startDate, endDate);
        } else {
            totalSessions = attendanceRepository.countDistinctEvents();
        }

        if (totalSessions <= 0) {
            return List.of();
        }

        List<Object[]> rows = attendanceRepository.countPresentGroupedByPolitician();
        List<RankingItemResponse> items = new ArrayList<>();

        for (Object[] row : rows) {
            Long politicianId = ((Number) row[0]).longValue();
            long present = ((Number) row[1]).longValue();

            Politician p = politicianRepository.findById(politicianId).orElse(null);
            if (p == null) {
                continue;
            }
            if (!matchesFilters(p, state, party, position, name)) {
                continue;
            }

            BigDecimal rate = BigDecimal.valueOf(present * 100.0 / totalSessions)
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

        return finalizeRanking(items, order);
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