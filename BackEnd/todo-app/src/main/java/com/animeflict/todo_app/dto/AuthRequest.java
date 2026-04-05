package com.animeflict.todo_app.dto;

public record AuthRequest(
        String username,
        String password,
        String fullName,
        String role
) {
}
