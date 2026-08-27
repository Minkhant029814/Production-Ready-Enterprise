package com.security.jwtdemo.dto.authDto;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
