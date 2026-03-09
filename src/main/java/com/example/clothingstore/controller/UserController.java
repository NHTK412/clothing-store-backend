package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.user.UserResponseDTO;
import com.example.clothingstore.service.UserService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            HttpServletRequest request) {

        System.out.println("Username: " + userDetails.getUsername());

        // String usename = userDetails.getUsername();

        Integer userId = userDetails.getUserId();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        userService.getUserById(userId),
                        request.getRequestURI()));
    }

}
