package com.renderhub.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.renderhub.backend.model.AssetVersion;

@Service
public class FileService {
    
    @Value("${FILES_BASE_DIR}")
    private String baseFileDirectory;

    
    public void createDirectoriesForAssetVersion(AssetVersion assetVersion) throws IOException{
        Path path = generatePath(assetVersion);
        Files.createDirectories(path.getParent());
    }

    public void storeFileForAsset(AssetVersion assetVersion, MultipartFile file) throws IOException{
        Path path = generatePath(assetVersion);
        Files.copy(file.getInputStream(), path);
    }

    public void deleteAssetVersion(AssetVersion assetVersion) throws IOException{
        Path path = generatePath(assetVersion);
        FileUtils.deleteDirectory(path.getParent().toFile());
    }

    public Path generatePath(AssetVersion assetVersion){
        String directoryPath = baseFileDirectory + "/" + assetVersion.getId();
        String fileName = "file." + assetVersion.getExtension();
        Path path = Path.of(directoryPath + "/" + fileName);
        return path;
    }

    public Long getFileSize(AssetVersion assetVersion) throws IOException {
        Path path = generatePath(assetVersion);
        return Files.size(path);
    }
}
