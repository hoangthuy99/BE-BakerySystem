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
    // PJ3: Thêm để tạo logic tính số lượng chỉ thị sản xuất
    private final OrderItemRepository orderItemRepository;


    @Override
    public FactoryRequest create(FactoryRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int finalRequestQuantity;

        // BÁNH → Auto tính
        if (Boolean.FALSE.equals(product.getAlcoholic())) {
            finalRequestQuantity = calculateAutoRequestQuantity(product.getId());
        }
        // ĐỒ UỐNG / ALCOHOL → Không auto tính
        else {
            finalRequestQuantity = dto.getRequestQuantity();
        }

        FactoryRequest request = FactoryRequest.builder()
                .productId(product.getId())
                .productName(product.getName())
                .requestQuantity(finalRequestQuantity)
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

    @Override
    public FactoryRequest updateStatus(Long requestId, FactoryRequestStatus status) {

        FactoryRequest request = factoryRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Factory request not found"));

        // Logic đặc biệt: khi nhận bánh
        if (status == FactoryRequestStatus.DELIVERED
                && request.getStatus() != FactoryRequestStatus.DELIVERED) {

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

    private int calculateAutoRequestQuantity(Long productId) {

        System.out.println("[AUTO_ORDER] ===== START ===== productId=" + productId);

        // 1. Tính khoảng thời gian hôm nay
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        System.out.println("[AUTO_ORDER] startOfToday=" + startOfToday + ", endOfToday=" + endOfToday);

        // 2. Đã bán hôm nay
        int soldToday = orderItemRepository.getSoldInRange(
                productId,
                startOfToday,
                endOfToday
        );

        System.out.println("[AUTO_ORDER] soldToday=" + soldToday);

        // 3. Tổng số đã bán
        long totalSold = orderItemRepository.getTotalSold(productId);
        System.out.println("[AUTO_ORDER] totalSold=" + totalSold);

        // 4. Ngày bán đầu & cuối
        LocalDateTime firstSold = orderItemRepository.getFirstSoldDate(productId);
        LocalDateTime lastSold = orderItemRepository.getLastSoldDate(productId);

        System.out.println("[AUTO_ORDER] firstSold=" + firstSold + ", lastSold=" + lastSold);

        // Nếu chưa từng bán → sản xuất tối thiểu
        if (firstSold == null || lastSold == null || totalSold == 0) {
            System.out.println("[AUTO_ORDER] No sale history → return 10");
            System.out.println("[AUTO_ORDER] ===== END =====");
            return 10;
        }

        // 5. Số ngày bán (ít nhất là 1)
        long days = Math.max(
                java.time.Duration.between(firstSold, lastSold).toDays() + 1,
                1
        );

        double averagePerDay = (double) totalSold / days;

        System.out.println("[AUTO_ORDER] days=" + days + ", averagePerDay=" + averagePerDay);

        // 6. Công thức bạn yêu cầu
        int suggested = (int) Math.ceil(averagePerDay - soldToday);
        int result = Math.max(suggested, 10);

        System.out.println("[AUTO_ORDER] suggested=" + suggested + ", finalResult=" + result);
        System.out.println("[AUTO_ORDER] ===== END =====");

        return result;
    }

}
