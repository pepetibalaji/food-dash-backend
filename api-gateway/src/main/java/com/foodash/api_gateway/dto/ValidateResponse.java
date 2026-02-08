package com.foodash.api_gateway.dto;

import lombok.Data;

@Data
public class ValidateResponse {
    private boolean valid;
    private String userId;
    private String role;

}
