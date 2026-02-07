package com.fooddash.auth_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddash.auth_service.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(UUID userId);

    RefreshToken save(com.fooddash.auth_service.entity.RefreshToken refreshToken);

}
