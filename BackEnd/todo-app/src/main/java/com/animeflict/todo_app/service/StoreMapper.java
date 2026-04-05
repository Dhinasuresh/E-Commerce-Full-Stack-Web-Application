package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.CustomerPayment;
import com.animeflict.todo_app.model.Sale;
import com.animeflict.todo_app.model.SaleItem;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class StoreMapper {
    public static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;
    private static final DateTimeFormatter BILL_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private StoreMapper() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String cleanText(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    public static String cleanOptionalText(String value) {
        return isBlank(value) ? null : value.trim();
    }

    public static String generateBillNumber() {
        return "BILL-" + LocalDateTime.now().format(BILL_FORMATTER);
    }

    public static Map<String, Object> toSaleSummary(Sale sale) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", sale.getId());
        payload.put("billNumber", sale.getBillNumber());
        payload.put("saleDate", sale.getSaleDate());
        payload.put("subtotal", sale.getSubtotal());
        payload.put("discount", sale.getDiscount());
        payload.put("grandTotal", sale.getGrandTotal());
        payload.put("paidAmount", sale.getPaidAmount());
        payload.put("dueAmount", sale.getDueAmount());
        payload.put("paymentStatus", sale.getPaymentStatus());
        payload.put("paymentMethod", sale.getPaymentMethod());
        payload.put("note", sale.getNote());
        payload.put("customerId", sale.getCustomer() == null ? null : sale.getCustomer().getId());
        payload.put("customerName", sale.getCustomer() == null ? "Walk-in Customer" : sale.getCustomer().getName());
        payload.put("items", sale.getItems().stream().map(StoreMapper::toSaleItemSummary).toList());
        return payload;
    }

    public static Map<String, Object> toSaleItemSummary(SaleItem item) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", item.getId());
        payload.put("productId", item.getProduct().getId());
        payload.put("productName", item.getProductName());
        payload.put("batchNumber", item.getBatchNumber());
        payload.put("quantity", item.getQuantity());
        payload.put("unitPrice", item.getUnitPrice());
        payload.put("lineTotal", item.getLineTotal());
        return payload;
    }

    public static Map<String, Object> toPaymentSummary(CustomerPayment payment) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", payment.getId());
        payload.put("amount", payment.getAmount());
        payload.put("paymentDate", payment.getPaymentDate());
        payload.put("paymentMethod", payment.getPaymentMethod());
        payload.put("note", payment.getNote());
        payload.put("recordedBy", payment.getRecordedBy());
        return payload;
    }
}
