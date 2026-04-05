package com.animeflict.todo_app.controller;

import com.animeflict.todo_app.dto.AuthRequest;
import com.animeflict.todo_app.model.User;
import com.animeflict.todo_app.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest request) {
        return service.login(request.username(), request.password());
    }

    @PostMapping("/register")
    public User register(@RequestBody AuthRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(request.password());
        user.setFullName(request.fullName());
        user.setRole(request.role());
        return service.register(user);
    }
}
