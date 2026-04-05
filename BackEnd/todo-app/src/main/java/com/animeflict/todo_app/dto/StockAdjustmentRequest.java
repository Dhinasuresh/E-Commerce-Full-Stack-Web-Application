package com.animeflict.todo_app.dto;

public record StockAdjustmentRequest(
        Integer quantity,
        String adjustmentType,
        String reason,
        Long createdBy
) {
}
