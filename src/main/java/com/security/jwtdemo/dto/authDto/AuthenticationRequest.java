package com.security.jwtdemo.dto.authDto;

public record AuthenticationRequest(
        String email,
        String password
) {
}
