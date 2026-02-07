package com.fooddash.auth_service.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fooddash.auth_service.config.RsaKeyConfig;
import com.fooddash.auth_service.entity.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RsaKeyConfig rsa;

    public String generateAccessToken(User user) throws Exception {

        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .claim("email", user.getEmail())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(15, ChronoUnit.MINUTES)))
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                claims);

        jwt.sign(new RSASSASigner(rsa.getPrivateKeyObj()));
        return jwt.serialize();
    }

    public String generateRefreshToken(User user) {
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }

    public JWTClaimsSet validate(String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);

        RSASSAVerifier verifier = new RSASSAVerifier(
                (java.security.interfaces.RSAPublicKey) rsa.getPublicKeyObj());

        if (!jwt.verify(verifier)) {
            throw new RuntimeException("Invalid token");
        }

        return jwt.getJWTClaimsSet();
    }
}
