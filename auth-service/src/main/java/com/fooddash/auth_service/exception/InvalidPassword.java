package com.fooddash.auth_service.exception;

public class InvalidPassword extends RuntimeException {

    public InvalidPassword(String message) {
        super(message);
    }

}
