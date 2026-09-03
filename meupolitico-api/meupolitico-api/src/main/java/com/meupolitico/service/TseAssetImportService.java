package com.meupolitico.service;

import com.meupolitico.entity.Asset;
import com.meupolitico.entity.Politician;
import com.meupolitico.repository.AssetRepository;
import com.meupolitico.repository.PoliticianRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class TseAssetImportService {

    private static final Logger log = LoggerFactory.getLogger(TseAssetImportService.class);
    private static final Charset TSE_CHARSET = Charset.forName("ISO-8859-1");

    private final PoliticianRepository politicianRepository;
    private final AssetRepository assetRepository;

    @Value("${tse.assets.local-dir}")
    private String localDir;

    public TseAssetImportService(PoliticianRepository politicianRepository,
                                 AssetRepository assetRepository) {
        this.politicianRepository = politicianRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public int importElectionYear(int year) {
        Path dir = Path.of(localDir, String.valueOf(year));
        Path candFile = findCsv(dir, "consulta_cand");
        Path bemFile = findCsv(dir, "bem_candidato");

        // sqCandidato -> total bens
        Map<String, BigDecimal> totalsBySq = sumAssetsByCandidate(bemFile);
        // sqCandidato -> (nomeNormalizado|UF)
        Map<String, String> keyBySq = loadFederalCandidates(candFile);

        Map<String, Politician> politiciansByKey = new HashMap<>();
        for (Politician p : politicianRepository.findAll()) {
            String key = normalizeKey(p.getBallotName() != null ? p.getBallotName() : p.getName(), p.getState());
            politiciansByKey.putIfAbsent(key, p);
            // também tenta pelo nome civil
            politiciansByKey.putIfAbsent(normalizeKey(p.getName(), p.getState()), p);
        }

        int imported = 0;
        int unmatched = 0;

        for (Map.Entry<String, String> entry : keyBySq.entrySet()) {
            String sq = entry.getKey();
            String key = entry.getValue();
            BigDecimal total = totalsBySq.getOrDefault(sq, BigDecimal.ZERO);

            Politician politician = politiciansByKey.get(key);
            if (politician == null) {
                unmatched++;
                continue;
            }

            Optional<Asset> existing = assetRepository.findByPoliticianIdAndYear(politician.getId(), year);
            Asset asset = existing.orElseGet(Asset::new);
            asset.setPolitician(politician);
            asset.setYear(year);
            asset.setDeclaredValue(total);
            // se tiveres source/externalId:
            // asset.setSource("TSE");
            // asset.setExternalId("tse-" + year + "-" + sq);

            assetRepository.save(asset);
            imported++;
        }

        log.info("TSE assets {}: matched={}, unmatchedCandidates={}", year, imported, unmatched);
        return imported;
    }

    private Path findCsv(Path dir, String prefix) {
        try (var stream = Files.list(dir)) {
            return stream
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
                        return n.startsWith(prefix.toLowerCase(Locale.ROOT)) && n.endsWith(".csv");
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "CSV not found for prefix " + prefix + " in " + dir));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot list " + dir + ": " + e.getMessage(), e);
        }
    }

    private Map<String, BigDecimal> sumAssetsByCandidate(Path bemFile) {
        Map<String, BigDecimal> totals = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(bemFile), TSE_CHARSET))) {

            String header = reader.readLine();
            if (header == null) {
                return totals;
            }
            String[] cols = splitCsv(header);
            int idxSq = indexOf(cols, "SQ_CANDIDATO");
            int idxValor = indexOf(cols, "VR_BEM_CANDIDATO");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] c = splitCsv(line);
                if (c.length <= Math.max(idxSq, idxValor)) {
                    continue;
                }
                String sq = stripQuotes(c[idxSq]);
                BigDecimal value = parseMoney(stripQuotes(c[idxValor]));
                totals.merge(sq, value, BigDecimal::add);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed reading bens: " + e.getMessage(), e);
        }
        return totals;
    }

    private Map<String, String> loadFederalCandidates(Path candFile) {
        Map<String, String> keyBySq = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(candFile), TSE_CHARSET))) {

            String header = reader.readLine();
            if (header == null) {
                return keyBySq;
            }
            String[] cols = splitCsv(header);
            int idxSq = indexOf(cols, "SQ_CANDIDATO");
            int idxNome = indexOf(cols, "NM_URNA_CANDIDATO");
            int idxNomeCivil = indexOf(cols, "NM_CANDIDATO");
            int idxUf = indexOf(cols, "SG_UF");
            int idxCargo = indexOf(cols, "CD_CARGO");
            int idxDsCargo = indexOf(cols, "DS_CARGO");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] c = splitCsv(line);
                if (c.length <= Math.max(idxSq, Math.max(idxUf, idxCargo))) {
                    continue;
                }

                String cargoCode = stripQuotes(c[idxCargo]);
                String dsCargo = idxDsCargo >= 0 ? stripQuotes(c[idxDsCargo]).toUpperCase(Locale.ROOT) : "";
                boolean federal = "6".equals(cargoCode) || dsCargo.contains("DEPUTADO FEDERAL");
                if (!federal) {
                    continue;
                }

                String sq = stripQuotes(c[idxSq]);
                String nomeUrna = stripQuotes(c[idxNome]);
                String nomeCivil = stripQuotes(c[idxNomeCivil]);
                String uf = stripQuotes(c[idxUf]);

                String nome = (nomeUrna != null && !nomeUrna.isBlank()) ? nomeUrna : nomeCivil;
                keyBySq.put(sq, normalizeKey(nome, uf));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed reading candidatos: " + e.getMessage(), e);
        }
        return keyBySq;
    }

    private String normalizeKey(String name, String uf) {
        String n = name == null ? "" : name;
        n = Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        n = n.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        String u = uf == null ? "" : uf.trim().toUpperCase(Locale.ROOT);
        return n + "|" + u;
    }

    private String[] splitCsv(String line) {
        // TSE usa ; e campos entre aspas
        return line.split(";", -1);
    }

    private String stripQuotes(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.trim();
    }

    private int indexOf(String[] cols, String name) {
        for (int i = 0; i < cols.length; i++) {
            if (name.equalsIgnoreCase(stripQuotes(cols[i]))) {
                return i;
            }
        }
        throw new IllegalStateException("Column not found: " + name);
    }

    private BigDecimal parseMoney(String raw) {
        if (raw == null || raw.isBlank()) {
            return BigDecimal.ZERO;
        }
        // "1.234.567,89" ou "1234567.89"
        String n = raw.replace(".", "").replace(",", ".");
        try {
            return new BigDecimal(n);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}