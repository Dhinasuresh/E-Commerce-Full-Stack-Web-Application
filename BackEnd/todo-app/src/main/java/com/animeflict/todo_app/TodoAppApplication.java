package com.animeflict.todo_app;

import com.animeflict.todo_app.dto.ProductRequest;
import com.animeflict.todo_app.model.Customer;
import com.animeflict.todo_app.model.User;
import com.animeflict.todo_app.repository.CustomerRepository;
import com.animeflict.todo_app.repository.ProductRepository;
import com.animeflict.todo_app.repository.UserRepository;
import com.animeflict.todo_app.service.ProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class TodoAppApplication {
	private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}

	@Bean
	CommandLineRunner seedData(
			ProductRepository productRepository,
			ProductService productService,
			UserRepository userRepository,
			CustomerRepository customerRepository
	) {
		return args -> {
			if (userRepository.findByUsernameIgnoreCase("owner@fertilizer.com").isEmpty()) {
				User owner = new User();
				owner.setFullName("Store Owner");
				owner.setUsername("owner@fertilizer.com");
				owner.setPasswordHash(PASSWORD_ENCODER.encode("admin123"));
				owner.setRole("OWNER");
				userRepository.save(owner);
			}

			if (userRepository.findByUsernameIgnoreCase("staff@fertilizer.com").isEmpty()) {
				User staff = new User();
				staff.setFullName("Counter Staff");
				staff.setUsername("staff@fertilizer.com");
				staff.setPasswordHash(PASSWORD_ENCODER.encode("staff123"));
				staff.setRole("STAFF");
				userRepository.save(staff);
			}

			if (customerRepository.count() == 0) {
				Customer customerOne = new Customer();
				customerOne.setName("Ramesh Farms");
				customerOne.setPhone("9876543210");
				customerOne.setVillageOrAddress("Nandgaon");
				customerOne.setNotes("Regular wholesale buyer");
				customerRepository.save(customerOne);

				Customer customerTwo = new Customer();
				customerTwo.setName("Green Field Agro");
				customerTwo.setPhone("9123456780");
				customerTwo.setVillageOrAddress("Rampur");
				customerTwo.setNotes("Buys on credit during season");
				customerRepository.save(customerTwo);
			}

			if (productRepository.count() == 0) {
				productService.createProduct(new ProductRequest(
						"Urea",
						"IFFCO",
						"Nitrogen Fertilizer",
						"UREA-45KG-B1",
						"45 Kg Bag",
						"bag",
						"BATCH-UREA-101",
						LocalDate.now().plusMonths(12),
						new BigDecimal("1180.00"),
						new BigDecimal("1320.00"),
						42,
						8,
						"IFFCO Supply Hub",
						true
				));
				productService.createProduct(new ProductRequest(
						"DAP",
						"Coromandel",
						"Phosphatic Fertilizer",
						"DAP-50KG-B2",
						"50 Kg Bag",
						"bag",
						"BATCH-DAP-204",
						LocalDate.now().plusMonths(10),
						new BigDecimal("1285.00"),
						new BigDecimal("1450.00"),
						28,
						6,
						"Coromandel Distributor",
						true
				));
				productService.createProduct(new ProductRequest(
						"Potash",
						"Tata",
						"Potassium Fertilizer",
						"POTASH-50KG-C1",
						"50 Kg Bag",
						"bag",
						"BATCH-POT-331",
						LocalDate.now().plusMonths(9),
						new BigDecimal("980.00"),
						new BigDecimal("1125.00"),
						19,
						5,
						"Tata Agro Chemicals",
						true
				));
				productService.createProduct(new ProductRequest(
						"Micronutrient Mix",
						"AgriGold",
						"Speciality Fertilizer",
						"MICRO-10KG-A5",
						"10 Kg Pack",
						"pack",
						"BATCH-MICRO-118",
						LocalDate.now().plusMonths(7),
						new BigDecimal("420.00"),
						new BigDecimal("520.00"),
						12,
						4,
						"AgriGold Traders",
						true
				));
			}
		};
	}

}
