package com.animeflict.todo_app.service;

import com.animeflict.todo_app.dto.ProductRequest;
import com.animeflict.todo_app.dto.StockAdjustmentRequest;
import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.model.StockAdjustment;
import com.animeflict.todo_app.repository.ProductRepository;
import com.animeflict.todo_app.repository.StockAdjustmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    public ProductServiceImpl(ProductRepository productRepository, StockAdjustmentRepository stockAdjustmentRepository) {
        this.productRepository = productRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    public Product getRequiredProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
    }

    @Override
    public Product createProduct(ProductRequest request) {
        validateProductRequest(request, null);
        Product product = new Product();
        applyProduct(product, request);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, ProductRequest request) {
        Product existing = getRequiredProduct(id);
        validateProductRequest(request, id);
        applyProduct(existing, request);
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public Map<String, Object> adjustStock(Long id, StockAdjustmentRequest request) {
        Product product = getRequiredProduct(id);
        if (request == null || request.quantity() == null || request.quantity() <= 0) {
            throw new IllegalArgumentException("Adjustment quantity must be greater than zero.");
        }

        String type = StoreMapper.cleanText(request.adjustmentType(), "ADD");
        int updatedQuantity = product.getStockQuantity();
        if ("REMOVE".equalsIgnoreCase(type)) {
            updatedQuantity -= request.quantity();
            if (updatedQuantity < 0) {
                throw new IllegalArgumentException("Stock cannot go below zero.");
            }
            type = "REMOVE";
        } else {
            updatedQuantity += request.quantity();
            type = "ADD";
        }

        product.setStockQuantity(updatedQuantity);
        productRepository.save(product);

        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setProduct(product);
        adjustment.setAdjustmentType(type);
        adjustment.setQuantity(request.quantity());
        adjustment.setReason(StoreMapper.cleanText(request.reason(), "Manual update"));
        adjustment.setCreatedBy(request.createdBy());
        stockAdjustmentRepository.save(adjustment);

        return Map.of("message", "Stock updated successfully.", "product", product);
    }

    private void validateProductRequest(ProductRequest request, Long productId) {
        if (request == null) {
            throw new IllegalArgumentException("Product details are required.");
        }
        if (StoreMapper.isBlank(request.name())) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (StoreMapper.isBlank(request.brand())) {
            throw new IllegalArgumentException("Brand is required.");
        }
        if (StoreMapper.isBlank(request.category())) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (StoreMapper.isBlank(request.sku())) {
            throw new IllegalArgumentException("SKU is required.");
        }
        if (StoreMapper.isBlank(request.packSize())) {
            throw new IllegalArgumentException("Pack size is required.");
        }
        if (StoreMapper.isBlank(request.unit())) {
            throw new IllegalArgumentException("Unit is required.");
        }
        if (StoreMapper.isBlank(request.batchNumber())) {
            throw new IllegalArgumentException("Batch number is required.");
        }
        if (request.purchasePrice() == null || request.purchasePrice().signum() < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative.");
        }
        if (request.salePrice() == null || request.salePrice().signum() < 0) {
            throw new IllegalArgumentException("Sale price cannot be negative.");
        }
        if (request.stockQuantity() == null || request.stockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        if (request.lowStockThreshold() == null || request.lowStockThreshold() < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative.");
        }
        if (StoreMapper.isBlank(request.supplierName())) {
            throw new IllegalArgumentException("Supplier name is required.");
        }

        productRepository.findBySkuIgnoreCase(request.sku().trim())
                .filter(existing -> !existing.getId().equals(productId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("A product with this SKU already exists.");
                });
    }

    private void applyProduct(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setBrand(request.brand().trim());
        product.setCategory(request.category().trim());
        product.setSku(request.sku().trim());
        product.setPackSize(request.packSize().trim());
        product.setUnit(request.unit().trim());
        product.setBatchNumber(request.batchNumber().trim());
        product.setExpiryDate(request.expiryDate());
        product.setPurchasePrice(request.purchasePrice().setScale(2, StoreMapper.MONEY_ROUNDING));
        product.setSalePrice(request.salePrice().setScale(2, StoreMapper.MONEY_ROUNDING));
        product.setStockQuantity(request.stockQuantity());
        product.setLowStockThreshold(request.lowStockThreshold());
        product.setSupplierName(request.supplierName().trim());
        product.setActive(request.active() == null || request.active());
    }
}
