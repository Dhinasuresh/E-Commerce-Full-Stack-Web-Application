package com.animeflict.todo_app.repository;

import com.animeflict.todo_app.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByOrderBySaleDateDescIdDesc();
    List<Sale> findByCustomerIdOrderBySaleDateDescIdDesc(Long customerId);
    List<Sale> findBySaleDateBetweenOrderBySaleDateDescIdDesc(LocalDate from, LocalDate to);
}
