package com.animeflict.todo_app.service;

import com.animeflict.todo_app.repository.CustomerRepository;
import com.animeflict.todo_app.repository.ProductRepository;
import com.animeflict.todo_app.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReportServiceImpl(
            SaleRepository saleRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository
    ) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<?> getSalesReport(LocalDate from, LocalDate to) {
        LocalDate effectiveFrom = from == null ? LocalDate.now().minusDays(30) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now() : to;
        return saleRepository.findBySaleDateBetweenOrderBySaleDateDescIdDesc(effectiveFrom, effectiveTo).stream()
                .map(StoreMapper::toSaleSummary)
                .toList();
    }

    @Override
    public List<?> getDueReport() {
        return customerRepository.findByActiveTrueOrderByNameAsc().stream()
                .filter(customer -> customer.getCurrentDue() != null && customer.getCurrentDue().signum() > 0)
                .toList();
    }

    @Override
    public List<?> getLowStockReport() {
        return productRepository.findByActiveTrueOrderByNameAsc().stream()
                .filter(product -> product.getStockQuantity() <= product.getLowStockThreshold())
                .toList();
    }
}
