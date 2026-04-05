package com.animeflict.todo_app.controller;

import com.animeflict.todo_app.dto.ProductRequest;
import com.animeflict.todo_app.dto.StockAdjustmentRequest;
import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.service.ProductService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAllProducts();
    }

    @PostMapping
    public Product create(@RequestBody ProductRequest request) {
        return service.createProduct(request);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return service.updateProduct(id, request);
    }

    @PatchMapping("/{id}/stock")
    public Map<String, Object> adjustStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request) {
        return service.adjustStock(id, request);
    }

    @DeleteMapping("/{id}")
    public Product archive(@PathVariable Long id) {
        Product product = service.getRequiredProduct(id);
        ProductRequest request = new ProductRequest(
                product.getName(),
                product.getBrand(),
                product.getCategory(),
                product.getSku(),
                product.getPackSize(),
                product.getUnit(),
                product.getBatchNumber(),
                product.getExpiryDate(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStockQuantity(),
                product.getLowStockThreshold(),
                product.getSupplierName(),
                false
        );
        return service.updateProduct(id, request);
    }
}
