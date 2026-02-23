package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.DTO.OrderItemRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.OrderItem;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.FactoryRequestRepository;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderItemRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.FactoryRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FactoryRequestServiceImpl implements FactoryRequestService {

    private final FactoryRequestRepository factoryRequestRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;

     private final ZoneId businessZone;
     @Value("${request.default:10}")
     private int defaultRequest;

    @Override
    public FactoryRequest create(FactoryRequestDTO dto) {

        if (dto.getEtaAt() == null) {
            throw new IllegalArgumentException("etaAt must not be null");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int finalRequestQuantity = dto.getQuantity();

        ZoneId zone = businessZone != null
                ? businessZone
                : ZoneId.systemDefault();

        Instant etaInstant = dto.getEtaAt()
                .atZone(zone)
                .toInstant();

        FactoryRequest request = FactoryRequest.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(finalRequestQuantity)
                .deliveredQuantity(0)
                .inventoryApplied(false)
                .etaAt(etaInstant)
                .note(dto.getNote())
                .status(FactoryRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        log.info("Saving FactoryRequest: productId={}, qty={}, eta={}",
                request.getProductId(),
                request.getQuantity(),
                request.getEtaAt()
        );

        return factoryRequestRepository.save(request);
    }



    @Override
     public List<FactoryRequest> getAllRequestFactoryByDateAndIsActive(LocalDate date, FactoryRequestStatus status) {
       return factoryRequestRepository.findByDateAndStatus(date, status);
     }
    @Override
     public FactoryRequest updateStatus(Long requestId, FactoryRequestStatus status) {
         log.info("updateStatus called: requestId={}, status={}", requestId, status);
         FactoryRequest request = factoryRequestRepository.findById(requestId)
             .orElseThrow(() -> new RuntimeException("Factory request not found"));
         request.setStatus(status);
         return factoryRequestRepository.save(request);
     }

     @Override
     public FactoryRequest receive(Long requestId, Integer quantity) {
         log.info("Receiving goods: requestId={}, quantity={}", requestId, quantity);

        FactoryRequest request = factoryRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Factory request not found"));

         if (request.getStatus() == FactoryRequestStatus.DELIVERED
                 || request.getStatus() == FactoryRequestStatus.CANCELLED) {
             throw new RuntimeException("Cannot receive goods for completed or cancelled request");
         }


         int totalDelivered = request.getDeliveredQuantity() + quantity;
         request.setDeliveredQuantity(totalDelivered);

         if (totalDelivered >= request.getQuantity()) {
             request.setStatus(FactoryRequestStatus.DELIVERED);
         } else {
             request.setStatus(FactoryRequestStatus.PARTIAL);
         }

         Inventory inventory = inventoryRepository
                 .findByProductId(request.getProductId())
                 .orElseThrow(() -> new RuntimeException("Inventory not found"));

         inventory.setCurrentQuantity(
                 inventory.getCurrentQuantity() + quantity
         );

         inventoryRepository.save(inventory);

        return factoryRequestRepository.save(request);
    }

    @Override
    public int getSuggestedQuantity(Long productId) {
        log.info(" getSuggestedQuantity called for productId={}", productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.TRUE.equals(product.getAlcoholic())) {
            return 0;
        }

        return calculateAutoRequestQuantity(productId);
    }

    private int calculateAutoRequestQuantity(Long productId) {
        log.info(" calculateAutoRequestQuantity(productId={})", productId);

         ZonedDateTime now = ZonedDateTime.now();

         // Order trong quá khứ (không truyền ngày)
         List<OrderItem> orderItemsPast =
             orderItemRepository.getOrderItemsByOrderTime(null, productId);

         // Order trong ngày hôm nay
         List<OrderItem> orderItemsToday =
             orderItemRepository.getOrderItemsByOrderTime(now.toLocalDate(), productId);

         // Không có order hôm nay → không đủ dữ liệu để auto request
         if (CollectionUtils.isEmpty(orderItemsToday)) {
             log.warn(" Chưa có order nào với sản phẩm {} trong ngày hôm nay", productId);
             throw new RuntimeException(
                 "Chưa có order nào với sản phẩm " + productId + " trong ngày hôm nay đến thời điểm hiện tại"
            );
         }
         // Tổng số lượng order trong quá khứ
         int avgOrderPast = CollectionUtils.isEmpty(orderItemsPast)
             ? 0
             : (int) Math.round(
             orderItemsPast.stream()
                 .map(OrderItem::getQuantity)
                 .filter(Objects::nonNull)
                 .mapToInt(Integer::intValue)
                 .average()
                 .orElse(0)
         );

         // Tổng số lượng order hôm nay
         int orderToday = orderItemsToday.stream()
             .map(OrderItem::getQuantity)
             .filter(Objects::nonNull)
             .mapToInt(Integer::intValue)
             .sum();

         // Nếu hôm nay bán nhiều hơn quá khứ → fallback về default
         if (avgOrderPast < orderToday) {
             return defaultRequest;
         }

         // Đề xuất số lượng cần request thêm
         int suggested = avgOrderPast - orderToday;

         // Không cho nhỏ hơn defaultRequest
         return Math.max(suggested, defaultRequest);
    }
}
