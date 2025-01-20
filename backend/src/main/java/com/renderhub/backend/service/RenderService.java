package com.renderhub.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renderhub.backend.enums.AssetVersionQuality;
import com.renderhub.backend.enums.RenderStatus;
import com.renderhub.backend.model.Asset;
import com.renderhub.backend.model.AssetVersion;

import jakarta.transaction.Transactional;

@Service
public class RenderService {
    

    @Autowired
    private FileService fileService;

    @Autowired
    private MessagePublisher messagePublisher;

    @Transactional
    public void sendAssetVersionForRender(Asset asset, AssetVersion assetVersion) throws JsonProcessingException, Exception{

        HashMap<String, Object> params = new HashMap<>();

        Optional<AssetVersion> originalAssetVersionOptional = asset.getVersions().stream().filter((av) -> av.getQuality().equals(AssetVersionQuality.ORIGINAL)).findFirst();

        if(!originalAssetVersionOptional.isPresent()){
            throw new Exception("Failed to find original asset version!");
        }

        AssetVersion originalAssetVersion = originalAssetVersionOptional.get();

        params.put("inputPath", fileService.generatePath(originalAssetVersion).toString());
        params.put("outputPath", fileService.generatePath(assetVersion).toString());
        params.put("arguments", qualityToArguments(assetVersion.getQuality()));
        params.put("assetVersionId", assetVersion.getId());


        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(params);
        
        messagePublisher.send(message);
    }

    private String qualityToArguments(AssetVersionQuality avq){
        switch (avq) {
            case LARGE: return  "-resize 90%";
            case MEDIUM: return  "-resize 50%";
            case SMALL: return  "-resize 20%";
            default: return  "-resize 100%";
        }
    }

}
