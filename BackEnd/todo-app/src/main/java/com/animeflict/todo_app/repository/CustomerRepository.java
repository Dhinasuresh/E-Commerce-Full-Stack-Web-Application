package com.animeflict.todo_app.repository;

import com.animeflict.todo_app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByActiveTrueOrderByNameAsc();
    Optional<Customer> findByPhone(String phone);
}
