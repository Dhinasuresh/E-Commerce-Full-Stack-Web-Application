package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.User;
import com.animeflict.todo_app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank() || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        if (userRepository.findByUsernameIgnoreCase(user.getUsername().trim()).isPresent()) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }

        User created = new User();
        created.setUsername(user.getUsername().trim());
        created.setPassword(user.getPassword());
        created.setFullName(user.getFullName() == null || user.getFullName().isBlank() ? "New Customer" : user.getFullName().trim());
        return userRepository.save(created);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User authenticate(String username, String password) {
        return userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim())
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }
}
