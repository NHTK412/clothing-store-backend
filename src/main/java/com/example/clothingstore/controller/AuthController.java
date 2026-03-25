package com.example.clothingstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.auth.AuthRegisterDTO;
import com.example.clothingstore.dto.auth.AuthResponseDTO;
import com.example.clothingstore.service.AuthService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
                        @Valid @RequestBody AuthRegisterDTO authRequestDTO,
                        HttpServletRequest request) {
                AuthResponseDTO authResponseDTO = authService.login_v2(
                                authRequestDTO.getUsername(),
                                authRequestDTO.getPassword());

                // false);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully logged in", authResponseDTO,
                                                request.getRequestURI()));
        }

        @PostMapping("/register-admin")
        public ResponseEntity<ApiResponse<AuthResponseDTO>> registerAdmin(
                        @Valid @RequestBody AuthRegisterDTO authRequestDTO,
                        @RequestParam String apiKey,
                        HttpServletRequest request) {
                AuthResponseDTO authResponseDTO = authService.registerAdmin(
                                authRequestDTO.getUsername(),
                                authRequestDTO.getPassword(),
                                apiKey);

                return ResponseEntity.ok(ApiResponse.created("Successfully registered admin", authResponseDTO,
                                request.getRequestURI()));

        }

        // Đăng ký tài khoản
        @PostMapping("/register")
        public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
                        @Valid @RequestBody AuthRegisterDTO authRequestDTO,
                        HttpServletRequest request) {

                AuthResponseDTO authResponseDTO = authService.register(authRequestDTO.getUsername(),
                                authRequestDTO.getPassword());
                return ResponseEntity.ok(ApiResponse.created("Successfully registered", authResponseDTO,
                                request.getRequestURI()));

        }

        // Đăng xuất
        // Refresh token cấp lại access token mới

        @PostMapping("/refresh-token")
        public ResponseEntity<ApiResponse<AuthResponseDTO>> getAccessTokenWithRefreshToken(
                        @RequestParam String refreshToken,
                        HttpServletRequest request) {
                AuthResponseDTO authResponseDTO = authService.getAccessTokenWithRefreshToken(refreshToken);
                return ResponseEntity.ok(ApiResponse.success("Successfully refreshed token", authResponseDTO,
                                request.getRequestURI()));
        }

}
