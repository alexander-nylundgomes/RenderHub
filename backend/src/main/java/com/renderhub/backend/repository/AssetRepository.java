package com.renderhub.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renderhub.backend.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{
    
}
