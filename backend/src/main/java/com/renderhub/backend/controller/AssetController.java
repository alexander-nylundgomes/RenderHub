package com.renderhub.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.renderhub.backend.enums.AssetVersionQuality;
import com.renderhub.backend.model.Asset;
import com.renderhub.backend.service.AssetService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/assets")
@CrossOrigin
public class AssetController {
    
    @Autowired
    private AssetService assetService;

    @GetMapping("")
    public ResponseEntity<List<Asset>> getAssets(){
        return assetService.getAssets();
    }

    @PostMapping("")
    public ResponseEntity<Asset> uploadAsset(
        @RequestParam String name,
        @RequestParam MultipartFile file
    ){
        return assetService.createAsset(name, file);
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<String> deleteAsset(
        @PathVariable Long assetId
    ){
        return assetService.deleteAsset(assetId);
    }

    @GetMapping("/{assetId}/{quality}/stream")
    public ResponseEntity<?> streamAsset(
        @PathVariable Long assetId,
        @PathVariable String quality
    ){
        AssetVersionQuality avq = AssetVersionQuality.valueOf(quality);
        return assetService.stream(assetId, avq);
    }

    @GetMapping("/{assetId}/{quality}/download")
    public ResponseEntity<?> download(
        @PathVariable Long assetId,
        @PathVariable String quality
    ){
        AssetVersionQuality avq = AssetVersionQuality.valueOf(quality);
        return assetService.download(assetId, avq);
    }
}
