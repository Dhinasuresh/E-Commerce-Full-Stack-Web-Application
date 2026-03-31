package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product getRequiredProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        product.setId(null);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existing = getRequiredProduct(id);
        validateProduct(product);
        existing.setName(product.getName().trim());
        existing.setCategory(product.getCategory().trim());
        existing.setDescription(product.getDescription().trim());
        existing.setPrice(product.getPrice());
        existing.setRating(product.getRating());
        existing.setImageUrl(product.getImageUrl().trim());
        existing.setFeatured(product.isFeatured());
        existing.setInventory(product.getInventory());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product details are required.");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (product.getDescription() == null || product.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("Image URL is required.");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (product.getInventory() < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative.");
        }
        if (product.getRating() < 0 || product.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }
    }
}
