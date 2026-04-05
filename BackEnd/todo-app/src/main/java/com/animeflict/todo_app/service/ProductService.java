package com.animeflict.todo_app.service;

import com.animeflict.todo_app.dto.ProductRequest;
import com.animeflict.todo_app.dto.StockAdjustmentRequest;
import com.animeflict.todo_app.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> getAllProducts();
    Product getRequiredProduct(Long id);
    Product createProduct(ProductRequest request);
    Product updateProduct(Long id, ProductRequest request);
    Map<String, Object> adjustStock(Long id, StockAdjustmentRequest request);
}
