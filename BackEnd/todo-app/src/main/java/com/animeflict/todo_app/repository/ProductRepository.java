package com.animeflict.todo_app.repository;

import com.animeflict.todo_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByNameAsc();
    Optional<Product> findBySkuIgnoreCase(String sku);
}
