package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Cart;

import java.util.List;

public interface CartService {
    Cart addToCart(Cart cart);
    List<Cart> getCart(Long userId);
    Cart updateQuantity(Long userId, Long productId, int quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}
