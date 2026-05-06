package com.cresensolutions.apigateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;


@Component
public class AuthHeaderForwardingFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final String HEADER_USERNAME = "X-Auth-Username";
    private static final String HEADER_ROLE = "X-Auth-Role";
    private static final String HEADER_EMAIL = "X-Auth-Email";
    private static final String HEADER_FULL_NAME = "X-Auth-FullName";
    private static final String HEADER_ACTIVE = "X-Auth-Active";

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String username = getAttributeAsString(request, HEADER_USERNAME);
        String role = getAttributeAsString(request, HEADER_ROLE);
        String email = getAttributeAsString(request, HEADER_EMAIL);
        String fullName = getAttributeAsString(request, HEADER_FULL_NAME);
        String active = getAttributeAsString(request, HEADER_ACTIVE);
        ServerRequest modifiedRequest = ServerRequest.from(request)
                .header(HEADER_USERNAME, username)
                .header(HEADER_ROLE, role)
                .header(HEADER_EMAIL, email)
                .header(HEADER_FULL_NAME, fullName)
                .header(HEADER_ACTIVE, active)
                .build();

        return next.handle(modifiedRequest);
    }

    private String getAttributeAsString(ServerRequest request, String attributeName) {
        Object value = request.servletRequest().getAttribute(attributeName);
        return value != null ? value.toString() : "";
    }
}
