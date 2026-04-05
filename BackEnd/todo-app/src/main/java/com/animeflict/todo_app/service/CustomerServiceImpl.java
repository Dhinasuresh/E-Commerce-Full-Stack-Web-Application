package com.animeflict.todo_app.service;

import com.animeflict.todo_app.dto.CustomerPaymentRequest;
import com.animeflict.todo_app.dto.CustomerRequest;
import com.animeflict.todo_app.model.Customer;
import com.animeflict.todo_app.model.CustomerPayment;
import com.animeflict.todo_app.repository.CustomerPaymentRepository;
import com.animeflict.todo_app.repository.CustomerRepository;
import com.animeflict.todo_app.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final SaleRepository saleRepository;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            CustomerPaymentRepository customerPaymentRepository,
            SaleRepository saleRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerPaymentRepository = customerPaymentRepository;
        this.saleRepository = saleRepository;
    }

    @Override
    public List<?> getAllCustomers() {
        return customerRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    public Object createCustomer(CustomerRequest request) {
        validateCustomerRequest(request, null);
        Customer customer = new Customer();
        applyCustomer(customer, request);
        return customerRepository.save(customer);
    }

    @Override
    public Object updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getRequiredCustomer(id);
        validateCustomerRequest(request, id);
        applyCustomer(customer, request);
        return customerRepository.save(customer);
    }

    @Override
    public Map<String, Object> getCustomerLedger(Long id) {
        Customer customer = getRequiredCustomer(id);
        return Map.of(
                "customer", customer,
                "sales", saleRepository.findByCustomerIdOrderBySaleDateDescIdDesc(id).stream()
                        .map(StoreMapper::toSaleSummary)
                        .toList(),
                "payments", customerPaymentRepository.findByCustomerIdOrderByPaymentDateDescCreatedAtDesc(id).stream()
                        .map(StoreMapper::toPaymentSummary)
                        .toList()
        );
    }

    @Override
    @Transactional
    public Map<String, Object> recordPayment(Long customerId, CustomerPaymentRequest request) {
        Customer customer = getRequiredCustomer(customerId);
        if (request == null || request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        BigDecimal paymentAmount = request.amount().setScale(2, StoreMapper.MONEY_ROUNDING);
        if (paymentAmount.compareTo(customer.getCurrentDue()) > 0) {
            throw new IllegalArgumentException("Payment cannot be greater than current due.");
        }

        CustomerPayment payment = new CustomerPayment();
        payment.setCustomer(customer);
        payment.setAmount(paymentAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(StoreMapper.cleanText(request.paymentMethod(), "CASH"));
        payment.setNote(StoreMapper.cleanOptionalText(request.note()));
        payment.setRecordedBy(request.recordedBy());
        customerPaymentRepository.save(payment);

        customer.setCurrentDue(customer.getCurrentDue().subtract(paymentAmount));
        customerRepository.save(customer);

        return Map.of(
                "message", "Payment recorded successfully.",
                "customer", customer,
                "payment", StoreMapper.toPaymentSummary(payment)
        );
    }

    Customer getRequiredCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
    }

    private void validateCustomerRequest(CustomerRequest request, Long customerId) {
        if (request == null) {
            throw new IllegalArgumentException("Customer details are required.");
        }
        if (StoreMapper.isBlank(request.name())) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (StoreMapper.isBlank(request.phone())) {
            throw new IllegalArgumentException("Customer phone is required.");
        }
        if (StoreMapper.isBlank(request.villageOrAddress())) {
            throw new IllegalArgumentException("Village or address is required.");
        }

        customerRepository.findByPhone(request.phone().trim())
                .filter(existing -> !existing.getId().equals(customerId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("A customer with this phone already exists.");
                });
    }

    private void applyCustomer(Customer customer, CustomerRequest request) {
        customer.setName(request.name().trim());
        customer.setPhone(request.phone().trim());
        customer.setVillageOrAddress(request.villageOrAddress().trim());
        customer.setNotes(StoreMapper.cleanOptionalText(request.notes()));
        customer.setActive(request.active() == null || request.active());
        if (customer.getCurrentDue() == null) {
            customer.setCurrentDue(BigDecimal.ZERO.setScale(2, StoreMapper.MONEY_ROUNDING));
        }
    }
}
