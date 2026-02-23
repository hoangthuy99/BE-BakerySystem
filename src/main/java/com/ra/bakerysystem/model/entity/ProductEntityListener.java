package com.ra.bakerysystem.model.entity;

import com.ra.bakerysystem.repository.InventoryRepository;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityListener {
    private static InventoryRepository inventoryRepository;

    @Autowired
    public void setInventoryRepository(InventoryRepository repo) {
        ProductEntityListener.inventoryRepository = repo;
    }

    @PostPersist
    public void afterProductCreated(Product product) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setCurrentQuantity(0);
        inventory.setMinThreshold(0);

        inventoryRepository.save(inventory);
    }
}
