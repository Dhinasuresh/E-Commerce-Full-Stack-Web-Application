package com.animeflict.todo_app.service;

import com.animeflict.todo_app.dto.SaleRequest;

import java.util.List;
import java.util.Map;

public interface SalesService {
    Map<String, Object> createSale(SaleRequest request);
    List<?> getSales();
}
