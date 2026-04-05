package com.animeflict.todo_app.repository;

import com.animeflict.todo_app.model.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    List<CustomerPayment> findByCustomerIdOrderByPaymentDateDescCreatedAtDesc(Long customerId);
}
