package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.service.FactoryRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/factory-requests")
@RequiredArgsConstructor
@Tag(name = "Factory Request API")
public class FactoryRequestController {

    private final FactoryRequestService factoryRequestService;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    @Operation(summary = "Create factory request")
    public FactoryRequest create(
            @RequestBody FactoryRequestDTO dto
    ) {
        return factoryRequestService.create(dto);
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    @Operation(summary = "Get all factory requests")
    public List<FactoryRequest> getAll() {
        return factoryRequestService.getAll();
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update factory request status")
    public FactoryRequest updateStatus(
            @PathVariable("id") Long requestId,
            @RequestParam FactoryRequestStatus status
    ) {
        return factoryRequestService.updateStatus(requestId, status);
    }

    // =====================================================
    // NEW API: GET AUTO SUGGESTED PRODUCTION QUANTITY
    // =====================================================
    @GetMapping("/suggested-quantity")
    @Operation(summary = "Get auto suggested production quantity for product")
    public Map<String, Integer> getSuggestedQuantity(
            @RequestParam Long productId
    ) {
        if (productId == null) throw new NullPointerException("productId is null");
        int quantity = factoryRequestService.getSuggestedQuantity(productId);
        return Map.of("quantity", quantity);
    }

}
