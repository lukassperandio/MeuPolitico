package com.meupolitico.service;

import com.meupolitico.dto.response.ComparedPoliticianResponse;
import com.meupolitico.dto.response.ComparisonResponse;
import com.meupolitico.entity.Asset;
import com.meupolitico.entity.Attendance;
import com.meupolitico.entity.Expense;
import com.meupolitico.entity.Politician;
import com.meupolitico.enums.AttendanceStatus;
import com.meupolitico.exception.ResourceNotFoundException;
import com.meupolitico.repository.AssetRepository;
import com.meupolitico.repository.AttendanceRepository;
import com.meupolitico.repository.ExpenseRepository;
import com.meupolitico.repository.PoliticianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ComparisonService {

    private final PoliticianRepository politicianRepository;
    private final ExpenseRepository expenseRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssetRepository assetRepository;

    public ComparisonService(PoliticianRepository politicianRepository,
                             ExpenseRepository expenseRepository,
                             AttendanceRepository attendanceRepository,
                             AssetRepository assetRepository) {
        this.politicianRepository = politicianRepository;
        this.expenseRepository = expenseRepository;
        this.attendanceRepository = attendanceRepository;
        this.assetRepository = assetRepository;
    }

    public ComparisonResponse compare(String idsParam) {
        List<Long> ids = parseIds(idsParam);

        if (ids.isEmpty() || ids.size() > 3) {
            throw new IllegalArgumentException("Comparison requires between 1 and 3 politician IDs");
        }

        List<ComparedPoliticianResponse> compared = new ArrayList<>();

        for (Long id : ids) {
            Politician politician = politicianRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Politician not found with id: " + id));

            BigDecimal totalExpenses = expenseRepository.findByPoliticianId(id).stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<Attendance> attendances = attendanceRepository.findByPoliticianId(id);
            long totalSessions = attendances.size();
            long present = attendances.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                    .count();
            double attendancePercentage = totalSessions == 0
                    ? 0.0
                    : Math.round((present * 10000.0) / totalSessions) / 100.0;

            List<Asset> assets = assetRepository.findByPoliticianIdOrderByYearAsc(id);
            BigDecimal latestAssetValue = null;
            Integer assetYear = null;
            if (!assets.isEmpty()) {
                Asset latest = assets.get(assets.size() - 1);
                latestAssetValue = latest.getDeclaredValue();
                assetYear = latest.getYear();
            }

            compared.add(new ComparedPoliticianResponse(
                    politician.getId(),
                    politician.getName(),
                    politician.getParty(),
                    politician.getState(),
                    politician.getPosition(),
                    totalExpenses,
                    attendancePercentage,
                    latestAssetValue,
                    assetYear
            ));
        }

        return new ComparisonResponse(compared);
    }

    private List<Long> parseIds(String idsParam) {
        if (idsParam == null || idsParam.isBlank()) {
            return List.of();
        }

        try {
            return Arrays.stream(idsParam.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .distinct()
                    .toList();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid politician IDs format. Use: ids=1,2,3");
        }
    }
}