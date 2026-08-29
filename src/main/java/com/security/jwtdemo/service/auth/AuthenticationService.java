package com.security.jwtdemo.service.auth;

import com.security.jwtdemo.authService.JwtService;
import com.security.jwtdemo.dto.authDto.AuthenticationRequest;
import com.security.jwtdemo.dto.authDto.AuthenticationResponse;
import com.security.jwtdemo.dto.authDto.RegisterRequest;
import com.security.jwtdemo.entity.mysql.RefreshToken;
import com.security.jwtdemo.entity.mysql.Role;
import com.security.jwtdemo.entity.mysql.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.security.jwtdemo.respository.mysqlRepository.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email address is already in use!");
        }

        User user = new User(
                request.firstName(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.USER
        );

        User savedUser = userRepository.save(user);

        // 👈 ၁။ savedUser ကိုယ်တိုင်က UserDetails ဖြစ်သလို savedUser.getId() ပါ ထည့်ပေးလိုက်ပါ
        String jwtToken = jwtService.generateToken(savedUser, savedUser.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.email()));

        // 👈 ၂။ userDetails အစား user ကို ပြောင်းသုံးလိုက်ပါ
        String jwtToken = jwtService.generateToken(user, user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}