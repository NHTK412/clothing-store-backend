package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.refund.RefundRequestDTO;
import com.example.clothingstore.service.RefundRequestService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/v1/refund-requests")
@RequiredArgsConstructor
public class RefundRequestController {

        private final RefundRequestService refundRequestService;

        @PostMapping
        public ResponseEntity<ApiResponse<Map<String, Object>>> createRefundRequest(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestBody RefundRequestDTO refundRequestDTO,
                        HttpServletRequest httpRequest) {
                Map<String, Object> result = refundRequestService.createRefundRequest(userDetails.getUserId(),
                                refundRequestDTO);
                return ResponseEntity.ok(ApiResponse.success(
                                "Refund request created successfully",
                                result,
                                httpRequest.getRequestURI()));
        }

}
