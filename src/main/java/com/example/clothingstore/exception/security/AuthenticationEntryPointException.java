package com.example.clothingstore.exception.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.clothingstore.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointException implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED,
                "You are not authenticated or token is invalid!", null, request.getRequestURI());

        String apiResponseJson = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(apiResponseJson);
        // response.setContentType("application/json;charset=UTF-8");
        // response.getWriter().write("{\"error\": \"You are not authenticated or token
        // is invalid!\"}");
    }
}