package com.animeflict.todo_app;

import com.animeflict.todo_app.model.Product;
import com.animeflict.todo_app.model.User;
import com.animeflict.todo_app.repository.ProductRepository;
import com.animeflict.todo_app.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TodoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}

	@Bean
	CommandLineRunner seedData(ProductRepository productRepository, UserRepository userRepository) {
		return args -> {
			if (userRepository.findByUsernameIgnoreCase("demo@shop.com").isEmpty()) {
				User demoUser = new User();
				demoUser.setFullName("Demo Shopper");
				demoUser.setUsername("demo@shop.com");
				demoUser.setPassword("demo123");
				userRepository.save(demoUser);
			}

			if (productRepository.count() == 0) {
				productRepository.save(new Product(null, "Urban Voyager Backpack", "Bags", "A weather-resistant backpack built for workdays, commutes, and weekend escapes.", 79.0, 4.8, "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80", true, 18));
				productRepository.save(new Product(null, "Aurora Wireless Headphones", "Audio", "Immersive over-ear headphones with deep bass and all-day battery life.", 149.0, 4.7, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80", true, 12));
				productRepository.save(new Product(null, "Studio Desk Lamp", "Home", "Warm ambient lighting with a minimalist metal finish for modern setups.", 59.0, 4.6, "https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=900&q=80", false, 25));
				productRepository.save(new Product(null, "Pulse Smartwatch", "Wearables", "Fitness tracking, notifications, and a sleek AMOLED face in one compact watch.", 199.0, 4.5, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80", true, 10));
				productRepository.save(new Product(null, "Cloud Knit Sneakers", "Fashion", "Lightweight everyday sneakers with soft cushioning and breathable knit uppers.", 109.0, 4.9, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80", false, 30));
				productRepository.save(new Product(null, "Stoneware Brew Set", "Kitchen", "Pour-over coffee kit with ceramic dripper, cup, and serving carafe.", 89.0, 4.4, "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80", false, 16));
			}
		};
	}

}
