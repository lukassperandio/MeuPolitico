package com.meupolitico.integration.camara;

import com.meupolitico.integration.camara.dto.CamaraDeputyListResponse;
import com.meupolitico.integration.camara.dto.CamaraDeputySummary;
import com.meupolitico.integration.camara.dto.CamaraLink;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CamaraDeputyClient {

    private final RestClient camaraRestClient;

    public CamaraDeputyClient(RestClient camaraRestClient) {
        this.camaraRestClient = camaraRestClient;
    }

    public List<CamaraDeputySummary> fetchAllDeputies() {
        List<CamaraDeputySummary> allDeputies = new ArrayList<>();
        int page = 1;
        boolean hasNext = true;

        while (hasNext) {
            final int currentPage = page;

            CamaraDeputyListResponse response = camaraRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/deputados")
                            .queryParam("itens", 100)
                            .queryParam("pagina", currentPage)
                            .queryParam("ordem", "ASC")
                            .queryParam("ordenarPor", "nome")
                            .build())
                    .retrieve()
                    .body(CamaraDeputyListResponse.class);

            if (response == null || response.dados() == null || response.dados().isEmpty()) {
                break;
            }

            allDeputies.addAll(response.dados());
            hasNext = hasNextPage(response.links());
            page++;
        }

        return allDeputies;
    }

    private boolean hasNextPage(List<CamaraLink> links) {
        if (links == null || links.isEmpty()) {
            return false;
        }

        return links.stream()
                .anyMatch(link -> "next".equalsIgnoreCase(link.rel()));
    }
}