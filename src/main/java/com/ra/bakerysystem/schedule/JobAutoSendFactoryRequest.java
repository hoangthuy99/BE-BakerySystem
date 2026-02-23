package com.ra.bakerysystem.schedule;

import com.ra.bakerysystem.common.FactoryRequestStatus;
import com.ra.bakerysystem.model.DTO.OrderItemRequestDTO;
import com.ra.bakerysystem.model.entity.FactoryRequest;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.OrderItem;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.FactoryRequestRepository;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.OrderItemRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class JobAutoSendFactoryRequest {

    private final FactoryRequestRepository factoryRequestRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ZoneId businessZone;
    @Value("${request.default:10}")
    private int defaultRequest;
    @Value("${app.idCake}")
    private String listIdCake;

    @Scheduled(cron = "0 0 12 * * *", zone = "${app.time.zone}")
    void jobAutoSendFactoryRequestFirst(){
        log.info("job request factory start ");
        List<Long> ids = Arrays.stream(listIdCake.split(","))
            .map(String::trim)
            .map(Long::valueOf)
            .toList();
        List<Inventory> inventories = inventoryRepository.getInventoryForRequestFactory(ids);
        List<Long> factoryRequests = factoryRequestRepository.findByDateAndStatus(null,FactoryRequestStatus.PENDING)
            .stream().map(FactoryRequest::getProductId).collect(Collectors.toList());

        for (Inventory inventory : inventories) {
            if (factoryRequests.contains(inventory.getProduct().getId())) {
                log.info("product id : {} đã được tạo request nhưng chưa xử lý", inventory.getProduct().getId());
                continue;
            }
            log.info("Tạo yêu cầu đặt hàng với sản phảm {} ", inventory.getProduct().getName());

            int finalRequestQuantity =  calculateAutoRequestQuantity(inventory.getProduct().getId());
            Instant etaInstant = Instant.now()
                .atZone(businessZone)
                .toInstant();
            FactoryRequest request = FactoryRequest.builder()
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(finalRequestQuantity)
                .deliveredQuantity(0)
                .inventoryApplied(false)
                .etaAt(etaInstant)
                .note("Tự động tạo yêu cầu")
                .status(FactoryRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();

            log.info(" SAVING FACTORY REQUEST: productId={}, qty={}",
                request.getProductId(),
                request.getQuantity()
            );
            factoryRequestRepository.save(request);
        }
    }

    @Scheduled(cron = "0 0 17 * * *",zone = "${app.time.zone}")
    void jobAutoSendFactoryRequestSecond(){
        log.info("job request factory start ");
        List<Long> ids = Arrays.stream(listIdCake.split(","))
            .map(String::trim)
            .map(Long::valueOf)
            .toList();
        List<Inventory> inventories = inventoryRepository.getInventoryForRequestFactory(ids);
        List<Long> factoryRequests = factoryRequestRepository.findByDateAndStatus(null,FactoryRequestStatus.PENDING)
            .stream().map(FactoryRequest::getProductId).collect(Collectors.toList());

        for (Inventory inventory : inventories) {
            if (factoryRequests.contains(inventory.getProduct().getId())) {
                log.info("product id : {} đã được tạo request nhưng chưa xử lý", inventory.getProduct().getId());
                continue;
            }
            log.info("Tạo yêu cầu đặt hàng với sản phẩm {} ", inventory.getProduct().getName());

            int finalRequestQuantity =  calculateAutoRequestQuantity(inventory.getProduct().getId());
            Instant etaInstant = Instant.now()
                .atZone(businessZone)
                .toInstant();
            FactoryRequest request = FactoryRequest.builder()
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(finalRequestQuantity)
                .deliveredQuantity(0)
                .inventoryApplied(false)
                .etaAt(etaInstant)
                .note("Tự động tạo yêu cầu")
                .status(FactoryRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();

            log.info("SAVING FACTORY REQUEST: productId={}, qty={}",
                request.getProductId(),
                request.getQuantity()
            );
            factoryRequestRepository.save(request);
        }
    }
    private int calculateAutoRequestQuantity(Long productId) {

        log.info("calculateAutoRequestQuantity(productId={})", productId);

        ZonedDateTime now = ZonedDateTime.now();

        List<OrderItem> orderItemsPast =
                orderItemRepository.getOrderItemsByOrderTime(null, productId);

        List<OrderItem> orderItemsToday =
                orderItemRepository.getOrderItemsByOrderTime(now.toLocalDate(), productId);


        if (CollectionUtils.isEmpty(orderItemsToday)) {
            log.warn("Chưa có order nào với product {} hôm nay → dùng defaultRequest", productId);
            return defaultRequest;
        }

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

        int orderToday = orderItemsToday.stream()
                .map(OrderItem::getQuantity)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        // Nếu hôm nay bán nhiều hơn trung bình → giữ mức tối thiểu
        if (avgOrderPast <= orderToday) {
            return defaultRequest;
        }

        int suggested = avgOrderPast - orderToday;

        return Math.max(suggested, defaultRequest);
    }


}
