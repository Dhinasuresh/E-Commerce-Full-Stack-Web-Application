package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product getRequiredProduct(Long id);
    Product saveProduct(Product product);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}
