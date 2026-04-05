package com.animeflict.todo_app.dto;

import java.math.BigDecimal;

public record CustomerPaymentRequest(
        BigDecimal amount,
        String paymentMethod,
        String note,
        Long recordedBy
) {
}
