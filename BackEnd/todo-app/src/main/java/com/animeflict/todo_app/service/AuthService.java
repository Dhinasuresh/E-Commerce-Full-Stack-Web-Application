package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.User;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String username, String password);
    User register(User user);
}
