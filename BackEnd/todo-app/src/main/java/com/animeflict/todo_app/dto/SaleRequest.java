package com.animeflict.todo_app.dto;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequest(
        Long customerId,
        List<SaleItemRequest> items,
        BigDecimal discount,
        BigDecimal paidAmount,
        String paymentMethod,
        String note,
        Long createdBy
) {
}
