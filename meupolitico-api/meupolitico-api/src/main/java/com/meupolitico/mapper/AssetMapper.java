package com.meupolitico.mapper;

import com.meupolitico.dto.request.AssetRequest;
import com.meupolitico.dto.response.AssetResponse;
import com.meupolitico.entity.Asset;
import com.meupolitico.entity.Politician;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public Asset toEntity(AssetRequest request, Politician politician) {
        Asset asset = new Asset();

        asset.setPolitician(politician);
        asset.setYear(request.year());
        asset.setDeclaredValue(request.declaredValue());
        asset.setSource(request.source());

        return asset;
    }

    public AssetResponse toResponse(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getPolitician().getId(),
                asset.getPolitician().getName(),
                asset.getYear(),
                asset.getDeclaredValue(),
                asset.getSource(),
                asset.getCreatedAt()
        );
    }
}