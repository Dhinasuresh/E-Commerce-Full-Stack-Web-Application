package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Order order);
    List<Order> getOrdersForUser(Long userId);
}
