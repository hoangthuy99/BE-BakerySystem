package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.FactoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactoryRequestRepository
        extends JpaRepository<FactoryRequest, Long> {
}

