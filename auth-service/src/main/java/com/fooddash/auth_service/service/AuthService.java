package com.fooddash.auth_service.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fooddash.auth_service.dto.AuthResponse;
import com.fooddash.auth_service.dto.LoginRequest;
import com.fooddash.auth_service.dto.RegisterRequest;
import com.fooddash.auth_service.dto.ValidateResponse;
import com.fooddash.auth_service.entity.RefreshToken;
import com.fooddash.auth_service.repository.RefreshTokenRepository;
import com.fooddash.auth_service.repository.UserRepository;
import com.fooddash.auth_service.security.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;
import com.fooddash.auth_service.entity.User;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    // REGISTER
    public UUID register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email exists");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(req.getEmail());
        user.setPasswordHash(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());

        userRepo.save(user);
        return user.getId();
    }

    // LOGIN
    public AuthResponse login(LoginRequest req) throws Exception {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        refreshRepo.save(new RefreshToken(
                UUID.randomUUID(),
                user.getId(),
                refresh,
                Instant.now().plus(7, ChronoUnit.DAYS)));

        return new AuthResponse(access, refresh);
    }

    // REFRESH
    public AuthResponse refresh(String refreshToken) throws Exception {

        RefreshToken token = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh"));

        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new RuntimeException("Expired refresh");

        User user = userRepo.findById(token.getUserId()).orElseThrow();

        refreshRepo.delete(token);

        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        refreshRepo.save(new RefreshToken(
                UUID.randomUUID(),
                user.getId(),
                newRefresh,
                Instant.now().plus(7, ChronoUnit.DAYS)));

        return new AuthResponse(newAccess, newRefresh);
    }

    // VALIDATE
    public ValidateResponse validate(String token) throws Exception {

        JWTClaimsSet claims = jwtService.validate(token);

        return new ValidateResponse(
                true,
                UUID.fromString(claims.getSubject()),
                claims.getStringClaim("role"));
    }
}
