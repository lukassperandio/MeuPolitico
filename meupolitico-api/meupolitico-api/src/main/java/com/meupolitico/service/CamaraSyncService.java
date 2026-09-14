package com.meupolitico.service;

import com.meupolitico.entity.Politician;
import com.meupolitico.integration.camara.CamaraDeputyClient;
import com.meupolitico.integration.camara.dto.CamaraDeputyDetail;
import com.meupolitico.integration.camara.dto.CamaraDeputySummary;
import com.meupolitico.repository.PoliticianRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CamaraSyncService {

    private static final Logger log = LoggerFactory.getLogger(CamaraSyncService.class);

    private final CamaraDeputyClient camaraDeputyClient;
    private final PoliticianRepository politicianRepository;

    public CamaraSyncService(CamaraDeputyClient camaraDeputyClient,
                             PoliticianRepository politicianRepository) {
        this.camaraDeputyClient = camaraDeputyClient;
        this.politicianRepository = politicianRepository;
    }

    @Transactional
    public int syncDeputies() {
        List<CamaraDeputySummary> deputies = camaraDeputyClient.fetchAllDeputies();
        log.info("Fetched {} deputies from Câmara API", deputies.size());

        int savedOrUpdated = 0;

        for (CamaraDeputySummary deputy : deputies) {

            String externalId = String.valueOf(deputy.id());
            Politician politician = politicianRepository.findByExternalId(externalId)
                    .orElseGet(Politician::new);

            politician.setExternalId(externalId);
            politician.setName(deputy.nome());

            politician.setBallotName(deputy.nome());
            politician.setPhotoUrl(deputy.urlFoto());
            politician.setParty(deputy.siglaPartido());
            politician.setState(deputy.siglaUf());
            politician.setPosition("Deputado Federal");
            politician.setStatus("Ativo");

            if (politician.getMandateStart() == null) {
                politician.setMandateStart(resolveMandateStart(deputy.id()));
            }

            politicianRepository.save(politician);
            savedOrUpdated++;
        }

        log.info("Sync finished. {} politicians saved/updated", savedOrUpdated);
        return savedOrUpdated;
    }

    private LocalDate resolveMandateStart(Long deputyId) {
        try {
            CamaraDeputyDetail detail = camaraDeputyClient.fetchDeputyDetail(deputyId);
            if (detail == null || detail.ultimoStatus() == null
                    || detail.ultimoStatus().data() == null) {
                return null;
            }

            String raw = detail.ultimoStatus().data();
            return LocalDate.parse(raw.substring(0, 10));

        } catch (Exception e) {
            log.warn("Failed to fetch detail for deputy {}: {}", deputyId, e.getMessage());
            return null;
        }
    }
}