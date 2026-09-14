package com.meupolitico.integration.camara.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CamaraMandateListResponse(List<CamaraMandateItem> dados) { }