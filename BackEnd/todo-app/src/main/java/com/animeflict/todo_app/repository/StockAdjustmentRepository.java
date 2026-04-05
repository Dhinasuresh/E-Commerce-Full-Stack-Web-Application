package com.animeflict.todo_app.repository;

import com.animeflict.todo_app.model.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
}
