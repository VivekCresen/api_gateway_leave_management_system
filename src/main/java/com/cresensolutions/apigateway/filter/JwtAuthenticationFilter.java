package com.cresensolutions.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USERNAME = "X-Auth-Username";
    private static final String HEADER_ROLE = "X-Auth-Role";
    private static final String HEADER_EMAIL = "X-Auth-Email";
    private static final String HEADER_FULL_NAME = "X-Auth-FullName";
    private static final String HEADER_ACTIVE = "X-Auth-Active";

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/actuator/health",
            "/actuator/info",
            "/actuator/prometheus",
            "/actuator/metrics",
            "/api/users/login",
            "/api/users/request-password-reset-otp",
            "/api/users/verify-password-reset-otp",
            "/api/users/reset-password",
            "/api/users/roles/summary",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    private final SecretKey signingKey;

    public JwtAuthenticationFilter(@Value("${security.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return isPublicPath(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration().before(new Date())) {
                sendUnauthorized(response, "Token expired");
                return;
            }

            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);
            String fullName = claims.get("fullName", String.class);
            Boolean active = claims.get("active", Boolean.class);

            if (username == null || username.isBlank()) {
                sendUnauthorized(response, "Token missing username");
                return;
            }

            // Store claims in request attributes for downstream services
            request.setAttribute(HEADER_USERNAME, username);
            request.setAttribute(HEADER_ROLE, role != null ? role : "");
            request.setAttribute(HEADER_EMAIL, email != null ? email : "");
            request.setAttribute(HEADER_FULL_NAME, fullName != null ? fullName : "");
            request.setAttribute(HEADER_ACTIVE, active != null ? active.toString() : "false");

            // Set Spring Security authentication context
            List<SimpleGrantedAuthority> authorities = (role != null && !role.isBlank())
                    ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    : List.of();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException e) {
            sendUnauthorized(response, "Invalid token: " + e.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    private boolean isPublicPath(String path) {
        // Exact match
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        // Prefix match for paths like /v3/api-docs/*, /swagger-ui/*
        return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }
}
