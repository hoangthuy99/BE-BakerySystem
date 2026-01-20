package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.FactoryRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.FactoryRequestRepository;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderItemRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.FactoryRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FactoryRequestServiceImpl implements FactoryRequestService {

    private final FactoryRequestRepository factoryRequestRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;

    // =========================
    // CREATE FACTORY REQUEST
    // =========================
    @Override
    public FactoryRequest create(FactoryRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int finalRequestQuantity;

        // BÁNH → auto tính
        if (Boolean.FALSE.equals(product.getAlcoholic())) {
            finalRequestQuantity = calculateAutoRequestQuantity(product.getId());
        }
        // ĐỒ UỐNG / ALCOHOL → dùng số FE gửi
        else {
            finalRequestQuantity = dto.getRequestQuantity();
        }

        FactoryRequest request = FactoryRequest.builder()
                .productId(product.getId())
                .productName(product.getName())
                .requestQuantity(finalRequestQuantity)
                .deliveredQuantity(0) // ✅ FIX: bắt buộc set mặc định
                .etaAt(dto.getEtaAt())
                .note(dto.getNote())
                .status(FactoryRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return factoryRequestRepository.save(request);
    }

    @Override
    public List<FactoryRequest> getAll() {
        return factoryRequestRepository.findAll();
    }

    // =====================================================
    // UPDATE FACTORY REQUEST STATUS
    // =====================================================
    @Override
    public FactoryRequest updateStatus(Long requestId, FactoryRequestStatus status) {

        FactoryRequest request = factoryRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Factory request not found"));

        /*
         * ========================= FIX BUG =========================
         * LỖI CŨ:
         *   - Chỉ cần status == DELIVERED là cộng kho
         *   - Khi reload / sync data, method này bị gọi lại
         *   - Dẫn tới inventory bị cộng lặp (+10 mỗi lần reload)
         *
         * SỬA:
         *   - CHỈ cộng kho khi:
         *       IN_PROGRESS  --->  DELIVERED
         *   - Đảm bảo mỗi request chỉ cộng kho DUY NHẤT 1 LẦN
         * ===========================================================
         */
        if (request.getStatus() == FactoryRequestStatus.PENDING
                && status == FactoryRequestStatus.DELIVERED) {

            Inventory inventory = inventoryRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            inventory.setCurrentQuantity(
                    inventory.getCurrentQuantity() + request.getRequestQuantity()
            );

            inventoryRepository.save(inventory);
        }

        request.setStatus(status);
        return factoryRequestRepository.save(request);
    }

    // =====================================================
    // API SUPPORT METHOD: FE gọi để lấy số lượng đề xuất
    // =====================================================
    @Override
    public int getSuggestedQuantity(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Đồ uống / alcohol → không auto
        if (Boolean.TRUE.equals(product.getAlcoholic())) {
            return 0; // FE tự nhập
        }

        return calculateAutoRequestQuantity(productId);
    }

    // =====================================================
    // CORE LOGIC – DÙNG CHUNG (KHÔNG GỌI TRỰC TIẾP TỪ FE)
    // =====================================================
    private int calculateAutoRequestQuantity(Long productId) {

        // 1. Khoảng thời gian hôm nay
        LocalDateTime startOfToday = LocalDateTime.now()
                .toLocalDate()
                .atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        // 2. Đã bán hôm nay
        int soldToday = orderItemRepository.getSoldInRange(
                productId,
                startOfToday,
                endOfToday
        );

        // 3. Tổng số đã bán
        long totalSold = orderItemRepository.getTotalSold(productId);

        // 4. Ngày bán đầu & cuối
        LocalDateTime firstSold = orderItemRepository.getFirstSoldDate(productId);
        LocalDateTime lastSold = orderItemRepository.getLastSoldDate(productId);

        // Chưa từng bán
        if (firstSold == null || lastSold == null || totalSold == 0) {
            return 10;
        }

        // 5. Số ngày bán
        long days = Math.max(
                java.time.Duration.between(firstSold, lastSold).toDays() + 1,
                1
        );

        double averagePerDay = (double) totalSold / days;

        // 6. Công thức SX
        int suggested = (int) Math.ceil(averagePerDay - soldToday);

        // đảm bảo luôn sản xuất tối thiểu 10
        return Math.max(suggested, 10);
    }
}
