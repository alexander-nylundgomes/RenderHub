package com.renderhub.backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.renderhub.backend.enums.AssetVersionQuality;
import com.renderhub.backend.enums.RenderStatus;
import com.renderhub.backend.exceptions.NotFoundException;
import com.renderhub.backend.model.Asset;
import com.renderhub.backend.model.AssetVersion;
import com.renderhub.backend.repository.AssetRepository;
import com.renderhub.backend.repository.AssetVersionRepository;

import jakarta.transaction.Transactional;

@Service
public class AssetService {
    
    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetVersionRepository assetVersionRepository;

    @Autowired
    private RenderService renderService;

    @Autowired
    private FileService fileService;


    public void handleAssetVersionFinishedRendered(Long assetVersionId, Boolean renderSuccess) throws NotFoundException, Exception{
        Optional<AssetVersion> assetVersionOptional = assetVersionRepository.findById(assetVersionId);
        
        if(!assetVersionOptional.isPresent()){
            throw new NotFoundException("Rendered asset from worker does not exist!");
        }

        AssetVersion assetVersion = assetVersionOptional.get();
        assetVersion.setStatus(renderSuccess ? RenderStatus.SUCCESS : RenderStatus.FAILED);
        assetVersion.setSize(fileService.getFileSize(assetVersion));
        assetVersion.setUpdatedAt(new Date());

        assetVersionRepository.save(assetVersion);
    }

    
    public ResponseEntity<String> deleteAsset(Long assetId){
        try {
            Optional<Asset> assetOptional = assetRepository.findById(assetId);
            
            if(!assetOptional.isPresent()){
                throw new NotFoundException("Asset not found");
            }

            Asset asset = assetOptional.get();

            for(AssetVersion assetVersion : asset.getVersions()){
                fileService.deleteAssetVersion(assetVersion);
            }

            assetRepository.delete(asset); // Because of CascadeType.ALL, this will delete the asset versions as well
            
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (NotFoundException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Asset>> getAssets(){
        
        List<Asset> assets = assetRepository.findAll();

        for(Asset asset : assets){
            asset.getVersions().size(); // Ensures loading of lazy relationship
        }

        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    public ResponseEntity<Asset> createAsset(String name, MultipartFile file){
        try {
            Asset asset = handleCreateAsset(name, file);
            return new ResponseEntity<>(asset, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Failed creating asset!");
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    private Asset handleCreateAsset(String name, MultipartFile file) throws Exception {
        
        // Create asset
        Asset asset = new Asset();
        asset.setName(name);

        // Create asset versions
        Set<AssetVersion> assetVersions = new HashSet<>();
        
        for(AssetVersionQuality quality : AssetVersionQuality.values()){
            
            AssetVersion assetVersion = new AssetVersion();
            assetVersion.setQuality(quality);
            
            String originalExtension = FilenameUtils.getExtension(file.getOriginalFilename());

            if(quality.equals(AssetVersionQuality.ORIGINAL)){
                assetVersion.setExtension(originalExtension);
                assetVersion.setStatus(RenderStatus.SUCCESS);
                assetVersion.setSize(file.getSize());
            }else{
                assetVersion.setStatus(RenderStatus.IN_QUEUE);

                // In case of PNG, we want to ensure alpha channel is not lost
                assetVersion.setExtension(originalExtension.equalsIgnoreCase("png") ? "png" : "jpg");
            }

            assetVersionRepository.save(assetVersion);
            assetVersions.add(assetVersion);
        }

        asset.setVersions(assetVersions);
        assetRepository.save(asset);

        // Store asset file
        for(AssetVersion assetVersion : asset.getVersions()){
            fileService.createDirectoriesForAssetVersion(assetVersion);

            if(assetVersion.getQuality().equals(AssetVersionQuality.ORIGINAL)){
                fileService.storeFileForAsset(assetVersion, file);
            }else{
                renderService.sendAssetVersionForRender(asset, assetVersion);
            }

        }

        return asset;
    }

    private AssetVersion getAssetVersion(Long assetId, AssetVersionQuality avq) throws NotFoundException{
        Optional<Asset> assetOptional = assetRepository.findById(assetId);

        if(!assetOptional.isPresent()){
            throw new NotFoundException("Could not find a matching asset");
        }
        
        Asset asset = assetOptional.get();
        Optional<AssetVersion> assetVersionOptional = asset.getVersions().stream().filter(av -> av.getQuality().equals(avq)).findFirst();
        
        if(!assetVersionOptional.isPresent()){
            throw new NotFoundException("Could not find a matching asset version");
        }

        AssetVersion assetVersion = assetVersionOptional.get();

        return assetVersion;
    }

    public ResponseEntity<?> download(Long assetId, AssetVersionQuality avq){
        try {


            AssetVersion assetVersion = getAssetVersion(assetId, avq);
            String filename = assetVersion.getId().toString() + "-" + assetVersion.getQuality().toString().toLowerCase() + "." + assetVersion.getExtension();

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            Path filePath = fileService.generatePath(assetVersion);
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        
            if (resource.exists()) {
                MediaType mediaType = (assetVersion.getExtension().equalsIgnoreCase("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG);
                return ResponseEntity.ok().headers(header).contentType(mediaType).body(resource);
            } else {
                throw new NotFoundException("File not found");
            }

        }
        catch(NotFoundException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

	public ResponseEntity<?> stream(Long assetId, AssetVersionQuality avq){
        try {
            AssetVersion assetVersion = getAssetVersion(assetId, avq);
            
            Path filePath = fileService.generatePath(assetVersion);
            Resource resource = new UrlResource(filePath.toUri());
        
            if (resource.exists()) {
                MediaType mediaType = (assetVersion.getExtension().equalsIgnoreCase("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG);
                return ResponseEntity.ok().contentType(mediaType).body(resource);
            } else {
                throw new NotFoundException("File not found");
            }

        }
        catch(NotFoundException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

	}
}
