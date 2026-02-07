package com.fooddash.auth_service.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateResponse {

    private boolean valid;
    private UUID userId;
    private String role;

}
