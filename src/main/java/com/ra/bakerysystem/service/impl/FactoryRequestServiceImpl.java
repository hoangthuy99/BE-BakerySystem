package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
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
//  private final SalesAverageCalculateRepository salesAverageCalculateRepository;

     private final ZoneId businessZone;
     @Value("${request.default:10}")
     private int defaultRequest;
    // =========================
    // CREATE FACTORY REQUEST
    // =========================
    @Override
    public FactoryRequest create(FactoryRequestDTO dto) {


        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        int finalRequestQuantity;

        finalRequestQuantity = dto.getRequestQuantity();
         Instant etaInstant = dto.getEtaAt()
             .atZone(businessZone)
             .toInstant();
        FactoryRequest request = FactoryRequest.builder()
            .productId(product.getId())
            .productName(product.getName())
            .requestQuantity(finalRequestQuantity)
            .deliveredQuantity(0)
            .inventoryApplied(false)
            .etaAt(etaInstant)
            .note(dto.getNote())
            .status(FactoryRequestStatus.PENDING)
            .createdAt(Instant.now())
            .build();

        log.error("🚨 SAVING FACTORY REQUEST: productId={}, qty={}",
            request.getProductId(),
            request.getRequestQuantity()
        );

        return factoryRequestRepository.save(request);
    }

//   @Override
//   public List<FactoryRequest> getAll() {
//       return factoryRequestRepository.findAll();
//   }

     @Override
     public List<FactoryRequest> getAllRequestFactoryByDateAndIsActive(LocalDate date, FactoryRequestStatus status) {
//       if (date == null && status == null) {
//           return factoryRequestRepository.findAll();
//       }
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
             || request.getStatus() == FactoryRequestStatus.PARTIAL
             || request.getStatus() == FactoryRequestStatus.CANCELLED) {
             throw new RuntimeException("Cannot receive goods for completed, partial or cancelled request");
         }

         int totalDelivered = request.getDeliveredQuantity() + quantity;
         request.setDeliveredQuantity(totalDelivered);

         if (totalDelivered >= request.getRequestQuantity()) {
             request.setStatus(FactoryRequestStatus.DELIVERED);
         } else {
             request.setStatus(FactoryRequestStatus.PARTIAL);
         }

         Inventory inventory = inventoryRepository.findById(request.getProductId()).orElse(null);
         if (inventory != null) {
             inventory.setCurrentQuantity(inventory.getCurrentQuantity() + quantity);
             inventoryRepository.save(inventory);
             log.info("Inventory Updated: product={}, added={}", request.getProductName(), quantity);
        }

        return factoryRequestRepository.save(request);
    }

    @Override
    public int getSuggestedQuantity(Long productId) {
        log.info("📊 getSuggestedQuantity called for productId={}", productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.TRUE.equals(product.getAlcoholic())) {
            return 0;
        }

        return calculateAutoRequestQuantity(productId);
    }

    private int calculateAutoRequestQuantity(Long productId) {
        log.info("🧮 calculateAutoRequestQuantity(productId={})", productId);
//       AverageType avgType;
//       LocalDate effectiveDate;
//       if (today.getMonthValue() == 1 && today.getDayOfMonth() == 1) {
//           avgType = AverageType.YEAR;
//           effectiveDate = today.minusYears(1).withDayOfYear(1);
//       } else if (today.getDayOfMonth() == 1) {
//           avgType = AverageType.MONTH;
//           effectiveDate = today.minusMonths(1).withDayOfMonth(1);
//       } else {
//           avgType = AverageType.DAY;
//           effectiveDate = today;
//       }
         // =========================
         // 2. Lấy snapshot trung bình
         // =========================
//         SaleAverageCalculateEntity saleAverageCalculate =
//             salesAverageCalculateRepository.findLatestAverageByDate(productId, avgType.name(), effectiveDate)
//                 .orElse(null);
//         if (saleAverageCalculate == null) {
//             log.warn("⚠️ No average snapshot found → default {}",defaultRequest);
//             return defaultRequest;
//         }
//         int average;
//         int average = saleAverageCalculate.getAverageQuantity();
         ZonedDateTime now = ZonedDateTime.now();

         // Order trong quá khứ (không truyền ngày)
         List<OrderItem> orderItemsPast =
             orderItemRepository.getOrderItemsByOrderTime(null, productId);

         // Order trong ngày hôm nay
         List<OrderItem> orderItemsToday =
             orderItemRepository.getOrderItemsByOrderTime(now.toLocalDate(), productId);

         // Không có order hôm nay → không đủ dữ liệu để auto request
         if (CollectionUtils.isEmpty(orderItemsToday)) {
             log.warn("⚠️ Chưa có order nào với sản phẩm {} trong ngày hôm nay", productId);
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
