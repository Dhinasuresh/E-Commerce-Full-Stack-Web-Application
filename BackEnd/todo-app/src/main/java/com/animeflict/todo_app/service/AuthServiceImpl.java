package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.authenticate(username, password);

        if (user != null) {
            response.put("status", "success");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
        } else {
            response.put("status", "error");
            response.put("message", "Invalid email or password.");
        }

        return response;
    }

    @Override
    public User register(User user) {
        return userService.register(user);
    }
}
