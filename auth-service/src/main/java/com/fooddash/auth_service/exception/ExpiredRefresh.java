package com.fooddash.auth_service.exception;

public class ExpiredRefresh extends RuntimeException {

    public ExpiredRefresh(String message) {
        super(message);
    }

}
