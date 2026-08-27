package com.security.jwtdemo.controller;

import com.security.jwtdemo.authService.JwtService;
import com.security.jwtdemo.dto.authDto.AuthenticationRequest;
import com.security.jwtdemo.dto.authDto.AuthenticationResponse;
import com.security.jwtdemo.dto.authDto.RefreshTokenRequest;
import com.security.jwtdemo.dto.authDto.RegisterRequest;
import com.security.jwtdemo.entity.mysql.RefreshToken;
import com.security.jwtdemo.respository.RefreshTokenRepository;
import com.security.jwtdemo.service.auth.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.security.jwtdemo.service.auth.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;
    private  final RefreshTokenService refreshTokenService;
    private  final RefreshTokenRepository refreshTokenRepository;
    private  final JwtService jwtService;

    public AuthController(AuthenticationService authService,
                          RefreshTokenService refreshTokenService,
                          RefreshTokenRepository refreshTokenRepository,
                          JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public  ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        return  ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public  ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return  refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user->{
                    String newAccessToken = jwtService.generateToken(user);
                    return  ResponseEntity.ok(AuthenticationResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(request.getRefreshToken())
                            .build());
                })
                .orElseThrow(()->new RuntimeException("Refresh token is not in database"));
    }


}
