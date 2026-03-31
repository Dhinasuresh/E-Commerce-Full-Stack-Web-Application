package com.animeflict.todo_app.repository;

import com.animeflict.todo_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
