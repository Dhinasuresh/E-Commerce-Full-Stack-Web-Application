package com.animeflict.todo_app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductRequest(
        String name,
        String brand,
        String category,
        String sku,
        String packSize,
        String unit,
        String batchNumber,
        LocalDate expiryDate,
        BigDecimal purchasePrice,
        BigDecimal salePrice,
        Integer stockQuantity,
        Integer lowStockThreshold,
        String supplierName,
        Boolean active
) {
}
