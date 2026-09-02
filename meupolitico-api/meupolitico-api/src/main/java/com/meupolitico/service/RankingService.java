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

    private static final int TOP_LIMIT = 50;

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
                                                    LocalDate startDate,
                                                    LocalDate endDate) {
        List<Object[]> rows = expenseRepository.findTopExpenseTotals();

        List<RankingItemResponse> ranking = new ArrayList<>();
        int positionIndex = 1;

        for (Object[] row : rows) {
            Long politicianId = ((Number) row[0]).longValue();
            BigDecimal total = row[1] != null
                    ? new BigDecimal(row[1].toString())
                    : BigDecimal.ZERO;

            Politician politician = politicianRepository.findById(politicianId).orElse(null);
            if (politician == null) {
                continue;
            }

            if (state != null && !state.isBlank()
                    && (politician.getState() == null
                    || !politician.getState().equalsIgnoreCase(state))) {
                continue;
            }
            if (party != null && !party.isBlank()
                    && (politician.getParty() == null
                    || !politician.getParty().equalsIgnoreCase(party))) {
                continue;
            }
            if (position != null && !position.isBlank()
                    && (politician.getPosition() == null
                    || !politician.getPosition().toLowerCase().contains(position.toLowerCase()))) {
                continue;
            }

            ranking.add(new RankingItemResponse(
                    positionIndex++,
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    total,
                    null
            ));
        }

        return ranking;
    }

    public List<RankingItemResponse> rankByAttendance(String state,
                                                      String party,
                                                      String position,
                                                      LocalDate startDate,
                                                      LocalDate endDate) {
        List<Object[]> rows = attendanceRepository.findTopAttendancePercentages();
        List<RankingItemResponse> ranking = new ArrayList<>();
        int pos = 1;

        for (Object[] row : rows) {
            Long politicianId = ((Number) row[0]).longValue();
            double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
            double present = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double percentage = total == 0 ? 0 : Math.round((present * 10000.0) / total) / 100.0;

            if (percentage <= 0) {
                continue;
            }

            Politician politician = politicianRepository.findById(politicianId).orElse(null);
            if (politician == null || !matchesFilters(politician, state, party, position)) {
                continue;
            }

            ranking.add(new RankingItemResponse(
                    pos++,
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    BigDecimal.valueOf(percentage),
                    total
            ));
        }

        return ranking;
    }

    public List<RankingItemResponse> rankByAssets(String state,
                                                  String party,
                                                  String position,
                                                  Integer year) {
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
            if (politician == null || !matchesFilters(politician, state, party, position)) {
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

        ranking.sort(Comparator.comparing(RankingItemResponse::value).reversed());

        List<RankingItemResponse> top = new ArrayList<>();
        int pos = 1;
        for (RankingItemResponse item : ranking) {
            if (pos > 50) {
                break;
            }
            top.add(new RankingItemResponse(
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
        return top;
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

    private List<RankingItemResponse> assignPositions(List<RankingItemResponse> ranking) {
        List<RankingItemResponse> result = new ArrayList<>();
        int position = 1;
        int limit = Math.min(TOP_LIMIT, ranking.size());

        for (int i = 0; i < limit; i++) {
            RankingItemResponse item = ranking.get(i);
            result.add(new RankingItemResponse(
                    position++,
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