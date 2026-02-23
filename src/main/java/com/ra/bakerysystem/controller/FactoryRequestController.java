package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.service.FactoryRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/app/factory-requests")
@RequiredArgsConstructor
@Tag(name = "Factory Request API")
public class FactoryRequestController {

    private final FactoryRequestService factoryRequestService;


    @PostMapping
    public FactoryRequest create(
            @Valid @RequestBody FactoryRequestDTO dto
    ) {
        return factoryRequestService.create(dto);
    }



    @GetMapping("")
    public List<FactoryRequest> getAllRequestFactoryByDateAndIsActive(
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "status", required = false) FactoryRequestStatus status
    ) {
        return factoryRequestService.getAllRequestFactoryByDateAndIsActive(date, status);
    }

    @PatchMapping("/{id}/status")
    public FactoryRequest updateStatus(
            @PathVariable("id") Long requestId,
            @RequestParam FactoryRequestStatus status
    ) {
        return factoryRequestService.updateStatus(requestId, status);
    }

    @PutMapping("/{id}/receive")
    public ResponseEntity<?> receive(
            @PathVariable Long id,
            @RequestBody FactoryRequestDTO dto
    ) {
        return ResponseEntity.ok(
                factoryRequestService.receive(id, dto.getDeliveredQuantity())
        );
    }
    @GetMapping("/suggested-quantity")
    public Map<String, Integer> getSuggestedQuantity(
            @RequestParam Long productId
    ) {
        if (productId == null) throw new NullPointerException("productId is null");
        int quantity = factoryRequestService.getSuggestedQuantity(productId);
        return Map.of("quantity", quantity);
    }

}
