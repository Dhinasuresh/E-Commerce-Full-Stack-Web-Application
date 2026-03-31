package com.animeflict.todo_app.controller;

import com.animeflict.todo_app.model.Cart;
import com.animeflict.todo_app.service.CartService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:4173",
        "http://127.0.0.1:4173"
})
public class CartController {
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public Cart add(@RequestBody Cart cart) {
        return service.addToCart(cart);
    }

    @GetMapping("/{userId}")
    public List<Cart> getCart(@PathVariable Long userId) {
        return service.getCart(userId);
    }

    @PatchMapping("/{userId}/items/{productId}")
    public Cart updateQuantity(@PathVariable Long userId, @PathVariable Long productId, @RequestBody Cart cart) {
        return service.updateQuantity(userId, productId, cart.getQuantity());
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public void removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        service.removeFromCart(userId, productId);
    }

    @DeleteMapping("/{userId}")
    public void clearCart(@PathVariable Long userId) {
        service.clearCart(userId);
    }
}
