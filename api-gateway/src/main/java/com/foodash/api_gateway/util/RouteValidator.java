package com.foodash.api_gateway.util;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    // Public endpoints (no token needed)
    public static final List<String> openApiEndpoints = List.of("/auth/register", "/auth/login", "/auth/refresh");

    // Check whether request is secured or not
    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
            .stream()
            .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
