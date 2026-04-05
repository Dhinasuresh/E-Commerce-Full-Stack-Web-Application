package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.model.Sale;
import com.animeflict.todo_app.repository.CustomerRepository;
import com.animeflict.todo_app.repository.ProductRepository;
import com.animeflict.todo_app.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DashboardServiceImpl(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Map<String, Object> getSummary() {
        LocalDate today = LocalDate.now();
        List<Sale> allSales = saleRepository.findAllByOrderBySaleDateDescIdDesc();
        List<Sale> todaySales = allSales.stream()
                .filter(sale -> today.equals(sale.getSaleDate()))
                .toList();
        List<Product> lowStockProducts = productRepository.findByActiveTrueOrderByNameAsc().stream()
                .filter(product -> product.getStockQuantity() <= product.getLowStockThreshold())
                .toList();

        BigDecimal todayRevenue = todaySales.stream()
                .map(Sale::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstandingDue = customerRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(customer -> customer.getCurrentDue() == null ? BigDecimal.ZERO : customer.getCurrentDue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "todaySalesCount", todaySales.size(),
                "todayRevenue", todayRevenue,
                "outstandingDue", outstandingDue,
                "activeProducts", productRepository.findByActiveTrueOrderByNameAsc().size(),
                "activeCustomers", customerRepository.findByActiveTrueOrderByNameAsc().size(),
                "lowStockCount", lowStockProducts.size(),
                "lowStockProducts", lowStockProducts.stream().limit(5).toList(),
                "recentSales", allSales.stream().limit(5).map(StoreMapper::toSaleSummary).toList()
        );
    }
}
