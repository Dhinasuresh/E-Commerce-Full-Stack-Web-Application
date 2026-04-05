package com.animeflict.todo_app.dto;

public record CustomerRequest(
        String name,
        String phone,
        String villageOrAddress,
        String notes,
        Boolean active
) {
}
