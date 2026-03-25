package com.example.clothingstore.exception.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerException implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        // response.setContentType("application/json;charset=UTF-8");
        // response.getWriter().write("{\"error\": \"Bạn không có quyền truy cập tài
        // nguyên này!\"}");

        // response.setContentType("application/json;charset=UTF-8");
        // response.getWriter().write("{\"success\": false, \"message\": \"Bạn không có
        // quyền truy cập!\"}");
        // response.getWriter().flush();
    }
}