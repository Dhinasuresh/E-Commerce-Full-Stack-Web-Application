package com.animeflict.todo_app.service;

import com.animeflict.todo_app.dto.SaleItemRequest;
import com.animeflict.todo_app.dto.SaleRequest;
import com.animeflict.todo_app.model.Customer;
import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.model.Sale;
import com.animeflict.todo_app.model.SaleItem;
import com.animeflict.todo_app.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SalesServiceImpl implements SalesService {
    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final CustomerServiceImpl customerService;

    public SalesServiceImpl(
            SaleRepository saleRepository,
            ProductService productService,
            CustomerServiceImpl customerService
    ) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public Map<String, Object> createSale(SaleRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("At least one sale item is required.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();
        for (SaleItemRequest itemRequest : request.items()) {
            if (itemRequest.productId() == null || itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
                throw new IllegalArgumentException("Each sale item must have a valid product and quantity.");
            }

            Product product = productService.getRequiredProduct(itemRequest.productId());
            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new IllegalArgumentException(product.getName() + " has only " + product.getStockQuantity() + " unit(s) in stock.");
            }

            BigDecimal unitPrice = product.getSalePrice().setScale(2, StoreMapper.MONEY_ROUNDING);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            SaleItem saleItem = new SaleItem();
            saleItem.setProduct(product);
            saleItem.setProductName(product.getName());
            saleItem.setBatchNumber(product.getBatchNumber());
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setUnitPrice(unitPrice);
            saleItem.setLineTotal(lineTotal);
            saleItems.add(saleItem);

            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal discount = request.discount() == null ? BigDecimal.ZERO : request.discount().setScale(2, StoreMapper.MONEY_ROUNDING);
        if (discount.signum() < 0) {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }
        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed subtotal.");
        }

        BigDecimal grandTotal = subtotal.subtract(discount);
        BigDecimal paidAmount = request.paidAmount() == null ? BigDecimal.ZERO : request.paidAmount().setScale(2, StoreMapper.MONEY_ROUNDING);
        if (paidAmount.signum() < 0) {
            throw new IllegalArgumentException("Paid amount cannot be negative.");
        }
        if (paidAmount.compareTo(grandTotal) > 0) {
            throw new IllegalArgumentException("Paid amount cannot exceed grand total.");
        }

        BigDecimal dueAmount = grandTotal.subtract(paidAmount);
        Customer customer = null;
        if (request.customerId() != null) {
            customer = customerService.getRequiredCustomer(request.customerId());
        }
        if (dueAmount.signum() > 0 && customer == null) {
            throw new IllegalArgumentException("A customer is required for due sales.");
        }

        Sale sale = new Sale();
        sale.setBillNumber(StoreMapper.generateBillNumber());
        sale.setCustomer(customer);
        sale.setSaleDate(LocalDate.now());
        sale.setSubtotal(subtotal);
        sale.setDiscount(discount);
        sale.setGrandTotal(grandTotal);
        sale.setPaidAmount(paidAmount);
        sale.setDueAmount(dueAmount);
        sale.setPaymentStatus(dueAmount.signum() > 0 ? "DUE" : "PAID");
        sale.setPaymentMethod(StoreMapper.cleanText(request.paymentMethod(), dueAmount.signum() > 0 ? "CREDIT" : "CASH"));
        sale.setNote(StoreMapper.cleanOptionalText(request.note()));
        sale.setCreatedBy(request.createdBy());
        sale.setItems(saleItems);

        Sale savedSale = saleRepository.save(sale);
        if (customer != null && dueAmount.signum() > 0) {
            customer.setCurrentDue(customer.getCurrentDue().add(dueAmount));
        }

        return Map.of("message", "Sale created successfully.", "sale", StoreMapper.toSaleSummary(savedSale));
    }

    @Override
    public List<?> getSales() {
        return saleRepository.findAllByOrderBySaleDateDescIdDesc().stream()
                .map(StoreMapper::toSaleSummary)
                .toList();
    }
}
