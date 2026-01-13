package com.checkmate.gateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfiguration {
    @Bean
    public RouteLocator RouteConfig(RouteLocatorBuilder builder) {
        return builder.routes()

                // Authentication Service
                .route(p -> p
                        .path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/auth/(?<remaining>.*)", "/${remaining}"))
                        .uri("lb://AUTHENTICATION-SERVICE")
                )

                // User Service
                .route(p -> p
                        .path("/api/users/**")
                        .filters(f -> f.rewritePath("/api/users/(?<remaining>.*)", "/${remaining}"))
                        .uri("lb://USERS-SERVICE")
                )

                // Matchmaking Service
                .route(p -> p
                        .path("/api/matchmaking/**")
                        .filters(f -> f.rewritePath("/api/matchmaking/(?<remaining>.*)", "/${remaining}"))
                        .uri("lb://MATCHMAKING-SERVICE")
                )

                // Match Service
                .route(p -> p
                        .path("/api/matches/**")
                        .filters(f -> f.rewritePath("/api/matches/(?<remaining>.*)", "/${remaining}"))
                        .uri("lb://MATCH-SERVICE")
                )
                .build();
    }
}