package com.animeflict.todo_app.service;

import com.animeflict.todo_app.dto.CustomerPaymentRequest;
import com.animeflict.todo_app.dto.CustomerRequest;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    List<?> getAllCustomers();
    Object createCustomer(CustomerRequest request);
    Object updateCustomer(Long id, CustomerRequest request);
    Map<String, Object> getCustomerLedger(Long id);
    Map<String, Object> recordPayment(Long customerId, CustomerPaymentRequest request);
}
