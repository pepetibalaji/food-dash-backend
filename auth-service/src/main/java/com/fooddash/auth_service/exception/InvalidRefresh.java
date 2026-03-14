package com.fooddash.auth_service.exception;

public class InvalidRefresh extends RuntimeException {

    public InvalidRefresh(String message) {
        super(message);
    }

}
