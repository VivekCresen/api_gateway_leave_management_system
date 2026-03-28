package com.cresensolutions.apigateway.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloControllerTest {

    @Test
    void shouldReturnHelloMessage() {
        HelloController controller = new HelloController();

        String message = controller.hello();

        assertEquals("Hello from API Gateway microservice", message);
    }
}
