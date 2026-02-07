package com.fooddash.auth_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import com.fooddash.auth_service.dto.*;
import com.fooddash.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Map<String, UUID> register(@RequestBody RegisterRequest req) {
        UUID id = authService.register(req);
        return Map.of("userId", id);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) throws Exception {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody Map<String, String> body) throws Exception {
        return authService.refresh(body.get("refreshToken"));
    }

    @PostMapping("/validate")
    public ValidateResponse validate(@RequestHeader("Authorization") String header) throws Exception {
        String token = header.replace("Bearer ", "");
        return authService.validate(token);
    }

}
