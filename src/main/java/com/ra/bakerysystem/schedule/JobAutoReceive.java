//package com.ra.bakerysystem.schedule;
//
//import com.ra.bakerysystem.common.FactoryRequestStatus;
//import com.ra.bakerysystem.model.entity.FactoryRequest;
//import com.ra.bakerysystem.model.entity.Inventory;
//import com.ra.bakerysystem.repository.FactoryRequestRepository;
//import com.ra.bakerysystem.repository.InventoryRepository;
//import com.ra.bakerysystem.repository.OrderItemRepository;
//import com.ra.bakerysystem.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Log4j2
//public class JobAutoReceive {
//    private final FactoryRequestRepository factoryRequestRepository;
//    private final ProductRepository productRepository;
//    private final InventoryRepository inventoryRepository;
//    private final OrderItemRepository orderItemRepository;
//
//    void autoReceive(){
//        List<FactoryRequest> requests = factoryRequestRepository.findByDateAndStatus(null,FactoryRequestStatus.PENDING);
//        for (FactoryRequest request : requests) {
//            int totalDelivered = request.getRequestQuantity();
//
//            if (totalDelivered >= request.getRequestQuantity()) {
//                request.setStatus(FactoryRequestStatus.DELIVERED);
//            } else {
//                request.setStatus(FactoryRequestStatus.PARTIAL);
//            }
//
//            Inventory inventory = inventoryRepository.findById(request.getProductId()).orElse(null);
//            if (inventory != null) {
//                inventory.setCurrentQuantity(inventory.getCurrentQuantity() + request.getRequestQuantity());
//                inventoryRepository.save(inventory);
//                log.info("Inventory Updated: product={}, added={}", request.getProductName(), quantity);
//            }
//
//        }
//
//    }
//
//}
