package com.fooddash.auth_service.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateResponse {

    private boolean valid;
    private UUID userId;
    private String role;

}
