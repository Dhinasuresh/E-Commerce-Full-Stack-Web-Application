package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Cart;
import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartServiceImpl(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Override
    public Cart addToCart(Cart cart) {
        if (cart == null || cart.getUserId() == null || cart.getProductId() == null || cart.getQuantity() <= 0) {
            throw new IllegalArgumentException("Valid user, product, and quantity are required.");
        }

        Product product = productService.getRequiredProduct(cart.getProductId());
        if (product.getInventory() <= 0) {
            throw new IllegalArgumentException("This product is out of stock.");
        }

        Cart savedCart = cartRepository.findByUserIdAndProductId(cart.getUserId(), cart.getProductId())
                .orElseGet(() -> {
                    Cart created = new Cart();
                    created.setUserId(cart.getUserId());
                    created.setProductId(cart.getProductId());
                    created.setQuantity(0);
                    return created;
                });

        int requestedQuantity = savedCart.getQuantity() + cart.getQuantity();
        if (requestedQuantity > product.getInventory()) {
            throw new IllegalArgumentException("Only " + product.getInventory() + " item(s) left in stock.");
        }

        savedCart.setQuantity(requestedQuantity);
        return cartRepository.save(savedCart);
    }

    @Override
    public List<Cart> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public Cart updateQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(userId, productId);
            return null;
        }

        Product product = productService.getRequiredProduct(productId);
        if (quantity > product.getInventory()) {
            throw new IllegalArgumentException("Only " + product.getInventory() + " item(s) left in stock.");
        }

        Cart cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found."));
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        cartRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(cartRepository::delete);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }
}
