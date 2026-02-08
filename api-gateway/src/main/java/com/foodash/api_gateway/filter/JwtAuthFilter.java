package com.foodash.api_gateway.filter;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // public endpoints
        if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh")) {
            return chain.filter(exchange);
        }

        // check header
        if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
            return onError(exchange, "No Authorization header", HttpStatus.SC_UNAUTHORIZED);
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        // call auth service
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(res -> chain.filter(exchange))
                .onErrorResume(e -> onError(exchange, "Invalid Token", HttpStatus.SC_UNAUTHORIZED));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, int scUnauthorized) {
        exchange.getResponse().setRawStatusCode(scUnauthorized);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
