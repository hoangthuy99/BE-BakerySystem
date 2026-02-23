package com.ra.bakerysystem.utils;

import com.ra.bakerysystem.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InventoryInitializer implements CommandLineRunner {

    @Autowired
    private InventoryService inventoryService;

    @Override
    public void run(String... args) {
        inventoryService.initMissingInventories();
    }
}