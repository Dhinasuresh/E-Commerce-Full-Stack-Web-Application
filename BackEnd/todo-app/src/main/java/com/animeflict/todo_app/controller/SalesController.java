package com.animeflict.todo_app.controller;

import com.animeflict.todo_app.dto.SaleRequest;
import com.animeflict.todo_app.service.SalesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public List<?> getSales() {
        return salesService.getSales();
    }

    @PostMapping
    public Map<String, Object> createSale(@RequestBody SaleRequest request) {
        return salesService.createSale(request);
    }
}
