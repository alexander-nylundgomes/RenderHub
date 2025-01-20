package com.renderhub.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renderhub.backend.enums.AssetVersionQuality;
import com.renderhub.backend.model.AssetVersion;

public interface AssetVersionRepository extends JpaRepository<AssetVersion, Long> {
    // @Query("SELECT e FROM Employee e WHERE e.name = :name and e.salary = :salary")

    // @Query("SELECT v FROM AssetVersion v WHERE v.asset_id = :assetId AND v.quality = :avq")
    // public Optional<AssetVersion> findByQualityAndAssetVersion(Long assetId, AssetVersionQuality avq);
}
