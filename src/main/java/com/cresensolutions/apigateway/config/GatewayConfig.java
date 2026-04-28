package com.cresensolutions.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("user-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> leaveServiceRoute() {
        return GatewayRouterFunctions.route("leave-service")
                .route(RequestPredicates.path("/api/leaves/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("leave-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> leaveServiceHolidaysRoute() {
        return GatewayRouterFunctions.route("leave-service-holidays")
                .route(RequestPredicates.path("/api/holidays/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("leave-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> leaveServiceChatbotRoute() {
        return GatewayRouterFunctions.route("leave-service-chatbot")
                .route(RequestPredicates.path("/api/chatbot/**"), HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("leave-service"))
                .build();
    }
}
