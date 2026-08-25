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
        List<Politician> politicians = filterPoliticians(state, party, position);
        List<Expense> expenses = expenseRepository.findAll();

        if (startDate != null) {
            expenses = expenses.stream()
                    .filter(e -> !e.getDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            expenses = expenses.stream()
                    .filter(e -> !e.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        Map<Long, BigDecimal> totals = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getPolitician().getId(),
                        Collectors.mapping(Expense::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<RankingItemResponse> ranking = new ArrayList<>();

        for (Politician politician : politicians) {
            BigDecimal total = totals.getOrDefault(politician.getId(), BigDecimal.ZERO);
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

        ranking.sort(Comparator.comparing(RankingItemResponse::value).reversed());
        return assignPositions(ranking);
    }

    public List<RankingItemResponse> rankByAttendance(String state,
                                                      String party,
                                                      String position,
                                                      LocalDate startDate,
                                                      LocalDate endDate) {
        List<Politician> politicians = filterPoliticians(state, party, position);
        List<Attendance> attendances = attendanceRepository.findAll();

        if (startDate != null) {
            attendances = attendances.stream()
                    .filter(a -> !a.getDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            attendances = attendances.stream()
                    .filter(a -> !a.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        Map<Long, List<Attendance>> byPolitician = attendances.stream()
                .collect(Collectors.groupingBy(a -> a.getPolitician().getId()));

        List<RankingItemResponse> ranking = new ArrayList<>();

        for (Politician politician : politicians) {
            List<Attendance> records = byPolitician.getOrDefault(politician.getId(), List.of());
            long total = records.size();
            long present = records.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                    .count();

            double percentage = total == 0 ? 0.0 : (present * 100.0) / total;
            percentage = Math.round(percentage * 100.0) / 100.0;

            ranking.add(new RankingItemResponse(
                    0,
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    BigDecimal.valueOf(percentage),
                    (double) total
            ));
        }

        ranking.sort(Comparator.comparing(RankingItemResponse::value).reversed());
        return assignPositions(ranking);
    }

    public List<RankingItemResponse> rankByAssets(String state,
                                                  String party,
                                                  String position,
                                                  Integer year) {
        List<Politician> politicians = filterPoliticians(state, party, position);
        List<Asset> assets = assetRepository.findAll();

        Map<Long, Asset> latestByPolitician = assets.stream()
                .filter(a -> year == null || a.getYear().equals(year))
                .collect(Collectors.toMap(
                        a -> a.getPolitician().getId(),
                        a -> a,
                        (a1, a2) -> a1.getYear() >= a2.getYear() ? a1 : a2
                ));

        List<RankingItemResponse> ranking = new ArrayList<>();

        for (Politician politician : politicians) {
            Asset asset = latestByPolitician.get(politician.getId());
            BigDecimal value = asset != null ? asset.getDeclaredValue() : BigDecimal.ZERO;
            Double assetYear = asset != null ? asset.getYear().doubleValue() : null;

            ranking.add(new RankingItemResponse(
                    0,
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    value,
                    assetYear
            ));
        }

        ranking.sort(Comparator.comparing(RankingItemResponse::value).reversed());
        return assignPositions(ranking);
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
        for (RankingItemResponse item : ranking) {
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