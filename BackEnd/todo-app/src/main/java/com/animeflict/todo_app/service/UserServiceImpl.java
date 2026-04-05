package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.User;
import com.animeflict.todo_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank() || user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        if (userRepository.findByUsernameIgnoreCase(user.getUsername().trim()).isPresent()) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }

        User created = new User();
        created.setUsername(user.getUsername().trim());
        created.setPasswordHash(PASSWORD_ENCODER.encode(user.getPasswordHash().trim()));
        created.setFullName(user.getFullName() == null || user.getFullName().isBlank() ? "New Customer" : user.getFullName().trim());
        created.setRole(user.getRole() == null || user.getRole().isBlank() ? "OWNER" : user.getRole().trim().toUpperCase());
        return userRepository.save(created);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User authenticate(String username, String password) {
        return userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim())
                .filter(User::isActive)
                .filter(user -> PASSWORD_ENCODER.matches(password == null ? "" : password, user.getPasswordHash()))
                .orElse(null);
    }
}
