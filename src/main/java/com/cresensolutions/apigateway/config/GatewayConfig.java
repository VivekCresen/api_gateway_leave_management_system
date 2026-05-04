package com.cresensolutions.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

@Configuration
public class GatewayConfig {

    private static final String USER_SERVICE_ID  = "USER-SERVICE";
    private static final String LEAVE_SERVICE_ID = "LEAVE-SERVICE";

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb(USER_SERVICE_ID))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "user-service-cb",
                        URI.create("forward:/fallback/user-service")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceCountriesRoute() {
        return GatewayRouterFunctions.route("user-service-countries")
                .route(RequestPredicates.path("/api/countries/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb(USER_SERVICE_ID))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "user-service-cb",
                        URI.create("forward:/fallback/user-service")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> leaveServiceRoute() {
        return GatewayRouterFunctions.route("leave-service")
                .route(RequestPredicates.path("/api/leaves/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb(LEAVE_SERVICE_ID))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "leave-service-cb",
                        URI.create("forward:/fallback/leave-service")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> leaveServiceHolidaysRoute() {
        return GatewayRouterFunctions.route("leave-service-holidays")
                .route(RequestPredicates.path("/api/holidays/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb(LEAVE_SERVICE_ID))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "leave-service-cb",
                        URI.create("forward:/fallback/leave-service")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> leaveServiceChatbotRoute() {
        return GatewayRouterFunctions.route("leave-service-chatbot")
                .route(RequestPredicates.path("/api/chatbot/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb(LEAVE_SERVICE_ID))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "chatbot-cb",
                        URI.create("forward:/fallback/chatbot")))
                .build();
    }
}
