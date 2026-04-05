package com.animeflict.todo_app.dto;

public record SaleItemRequest(
        Long productId,
        Integer quantity
) {
}
