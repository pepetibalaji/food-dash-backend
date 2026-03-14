package com.fooddash.auth_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import com.fooddash.auth_service.dto.AuthResponse;
import com.fooddash.auth_service.dto.LoginRequest;
import com.fooddash.auth_service.dto.RegisterRequest;
import com.fooddash.auth_service.dto.ValidateResponse;
import com.fooddash.auth_service.security.JwtService;
import com.fooddash.auth_service.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    // ✅ REGISTER TEST
    @Test
    void shouldRegisterUser() throws Exception {

        UUID userId = UUID.randomUUID();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(userId);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "test@fooddash.com",
                          "password": "password123",
                          "role": "CUSTOMER"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    // ✅ LOGIN TEST
    @Test
    void shouldLoginUser() throws Exception {

        AuthResponse response =
                new AuthResponse("access-token", "refresh-token");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "test@fooddash.com",
                          "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    // ✅ REFRESH TOKEN TEST
    @Test
    void shouldRefreshToken() throws Exception {

        AuthResponse response =
                new AuthResponse("new-access", "new-refresh");

        when(authService.refresh(eq("refresh-token")))
                .thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "refreshToken": "refresh-token"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    // ✅ VALIDATE TOKEN TEST
    @Test
    void shouldValidateToken() throws Exception {

        ValidateResponse response =
                new ValidateResponse(true, UUID.randomUUID(), "CUSTOMER");

        when(authService.validate(eq("valid-token")))
                .thenReturn(response);

        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }
}