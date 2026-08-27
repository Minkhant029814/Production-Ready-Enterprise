package com.security.jwtdemo.service.auth;

import com.security.jwtdemo.entity.mysql.RefreshToken;
import com.security.jwtdemo.entity.mysql.User;
import com.security.jwtdemo.respository.RefreshTokenRepository;
import com.security.jwtdemo.respository.mysqlRepository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private  Long refreshTokenDurationMs;

    private  final RefreshTokenRepository refreshTokenRepository;
    private  final UserRepository userRepository;

    public  RefreshTokenService(RefreshTokenRepository refreshTokenRepository,UserRepository userRepository){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    //Generate new Token (one user must have one token)
    @Transactional
    public RefreshToken createRefreshToken(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with id " + userId));

        //refresh token
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    //Checking Token expired or not
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now()) < 0 || token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired or revoked. Please a new signin request");

        }
        return token;
    }

}
