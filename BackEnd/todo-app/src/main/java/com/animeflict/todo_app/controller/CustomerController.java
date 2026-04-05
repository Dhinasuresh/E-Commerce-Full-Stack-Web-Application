package com.animeflict.todo_app.controller;

import com.animeflict.todo_app.dto.CustomerPaymentRequest;
import com.animeflict.todo_app.dto.CustomerRequest;
import com.animeflict.todo_app.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<?> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping
    public Object createCustomer(@RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @PutMapping("/{id}")
    public Object updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    @GetMapping("/{id}/ledger")
    public Map<String, Object> getLedger(@PathVariable Long id) {
        return customerService.getCustomerLedger(id);
    }

    @PostMapping("/{id}/payments")
    public Map<String, Object> recordPayment(@PathVariable Long id, @RequestBody CustomerPaymentRequest request) {
        return customerService.recordPayment(id, request);
    }
}
