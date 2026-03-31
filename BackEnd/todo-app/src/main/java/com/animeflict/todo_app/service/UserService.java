package com.animeflict.todo_app.service;

import com.animeflict.todo_app.model.User;

public interface UserService {
    User register(User user);
    User getUserById(Long id);
    User authenticate(String username, String password);
}
