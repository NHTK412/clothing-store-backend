package com.example.clothingstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.auth.AuthRequestDTO;
import com.example.clothingstore.dto.auth.AuthResponseDTO;
import com.example.clothingstore.service.AuthService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    // @Autowired
    // AuthService authService;

    private final AuthService authService;

    // @PostMapping("/login")
    // public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody
    // AuthRequestDTO authRequestDTO) {
    // AuthResponseDTO authResponseDTO =
    // authService.login(authRequestDTO.getUsername(),
    // authRequestDTO.getPassword());
    // return ResponseEntity.ok(new ApiResponse<AuthResponseDTO>(true, null,
    // authResponseDTO));
    // }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestParam(defaultValue = "false") Boolean admin,
            @Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.login(authRequestDTO.getUsername(), authRequestDTO.getPassword(),
                admin);
        return ResponseEntity.ok(new ApiResponse<AuthResponseDTO>(true, null, authResponseDTO));
    }

    // Đăng ký tài khoản
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody AuthRequestDTO authRequestDTO) {

        AuthResponseDTO authResponseDTO = authService.register(authRequestDTO.getUsername(),
                authRequestDTO.getPassword());
        return ResponseEntity.ok(new ApiResponse<AuthResponseDTO>(true, null, authResponseDTO));
    }

    @PostMapping("/login-admin")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> loginAdmin(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.loginAdmin(authRequestDTO.getUsername(),
                authRequestDTO.getPassword());
        return ResponseEntity.ok(new ApiResponse<AuthResponseDTO>(true, null, authResponseDTO));
    }
    // @PostMapping("/refresh-token")
    // public ResponseEntity<ApiResponse<AuthResponseDTO>>
    // getAccessTokenWithRefreshToken(@RequestParam String refreshToken) {

    // AuthResponseDTO authResponseDTO =
    // authService.getAccessTokenWithRefreshToken(refreshToken);

    // return ResponseEntity.ok(new ApiResponse<AuthResponseDTO>(true, null,
    // authResponseDTO));
    // }

}
