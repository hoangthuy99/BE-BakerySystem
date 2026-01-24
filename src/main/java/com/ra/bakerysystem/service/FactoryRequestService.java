package com.ra.bakerysystem.service;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface FactoryRequestService {

    FactoryRequest create(FactoryRequestDTO dto);

    List<FactoryRequest> getAll();

    List<FactoryRequest> getAllRequestFactoryByDateAndIsActive(LocalDate date) ;


    FactoryRequest updateStatus(Long requestId, FactoryRequestStatus status);

    // PJ3: Thêm để tính số lượng đề xuất chỉ thị xưởng sản xuất thêm
    int getSuggestedQuantity(Long productId);
}
