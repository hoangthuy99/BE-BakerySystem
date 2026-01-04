package com.ra.bakerysystem.service;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import java.util.List;

public interface FactoryRequestService {

    FactoryRequest create(FactoryRequestDTO dto);

    List<FactoryRequest> getAll();

    FactoryRequest updateStatus(Long id, FactoryRequestStatus status);
}

