package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.Cart;
import com.animeflict.todo_app.model.Order;
import com.animeflict.todo_app.model.OrderItem;
import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.repository.CartRepository;
import com.animeflict.todo_app.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Order placeOrder(Order order) {
        if (order == null || order.getUserId() == null) {
            throw new IllegalArgumentException("A user is required.");
        }

        List<Cart> cartItems = cartRepository.findByUserId(order.getUserId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }

        double totalAmount = 0;
        Order persistentOrder = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        persistentOrder.setUserId(order.getUserId());
        persistentOrder.setCustomerName(order.getCustomerName());
        persistentOrder.setEmail(order.getEmail());
        persistentOrder.setAddress(order.getAddress());
        persistentOrder.setStatus("Confirmed");
        persistentOrder.setPlacedAt(LocalDateTime.now());

        for (Cart cartItem : cartItems) {
            Product product = productService.getRequiredProduct(cartItem.getProductId());
            if (product.getInventory() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(product.getName() + " only has " + product.getInventory() + " item(s) left.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();

            int remainingInventory = product.getInventory() - cartItem.getQuantity();
            if (remainingInventory <= 0) {
                productService.deleteProduct(product.getId());
            } else {
                product.setInventory(remainingInventory);
                productService.saveProduct(product);
            }
        }

        persistentOrder.setItems(orderItems);
        persistentOrder.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(persistentOrder);
        cartRepository.deleteByUserId(order.getUserId());
        return savedOrder;
    }

    @Override
    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserIdOrderByPlacedAtDesc(userId);
    }
}
