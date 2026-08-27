package com.security.jwtdemo.dto.authDto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private  String refreshToken;
}
