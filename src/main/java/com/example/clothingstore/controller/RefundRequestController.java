package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.refund.CreateRefundPaymentDTO;
import com.example.clothingstore.dto.refund.RefundRequestDTO;
import com.example.clothingstore.dto.refund.RefundResponseDTO;
import com.example.clothingstore.dto.refund.RefundSummaryDTO;
import com.example.clothingstore.enums.RefundMethodEnum;
import com.example.clothingstore.enums.RefundRequestStatusEnum;
import com.example.clothingstore.service.RefundRequestService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/v1/refund-requests")
@RequiredArgsConstructor
public class RefundRequestController {

        private final RefundRequestService refundRequestService;

        @PreAuthorize("hasRole('CUSTOMER')")
        @PostMapping
        public ResponseEntity<ApiResponse<RefundResponseDTO>> createRefundRequest(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestBody RefundRequestDTO refundRequestDTO,
                        HttpServletRequest httpRequest) {
                // Map<String, Object> result =
                // refundRequestService.createRefundRequest(userDetails.getUserId(),
                // refundRequestDTO);
                // return ResponseEntity.ok(ApiResponse.success(
                // "Refund request created successfully",
                // result,
                // httpRequest.getRequestURI()));

                RefundResponseDTO refundResponseDTO = refundRequestService.createRefundRequest_v2(
                                userDetails.getUserId(),
                                refundRequestDTO);
                // throw new RuntimeException("Test exception handling");
                return ResponseEntity.ok(ApiResponse.success(
                                "Refund request created successfully",
                                refundResponseDTO,
                                httpRequest.getRequestURI()));

        }

        // getall
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<ApiResponse<Page<RefundSummaryDTO>>> getRefundRequests(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        HttpServletRequest httpRequest) {
                Pageable pageable = Pageable.ofSize(size).withPage(page - 1);
                Page<RefundSummaryDTO> refundRequests = refundRequestService.getAll(pageable);
                return ResponseEntity.ok(ApiResponse.success(
                                "Refund requests retrieved successfully",
                                refundRequests,
                                httpRequest.getRequestURI()));
        }

        // getDetail
        @GetMapping("/{refundRequestId}")
        public ResponseEntity<ApiResponse<RefundResponseDTO>> getById(
                        @PathVariable Long refundRequestId,
                        HttpServletRequest httpRequest) {
                RefundResponseDTO refundResponseDTO = refundRequestService.getById(refundRequestId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Refund request retrieved successfully",
                                                refundResponseDTO,
                                                httpRequest.getRequestURI()));
        }
        // update

        @PatchMapping("/{refundRequestId}/status")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RefundResponseDTO>> updateRefundRequestStatus(
                        @PathVariable Long refundRequestId,
                        @RequestParam RefundRequestStatusEnum status,
                        HttpServletRequest httpRequest) {
                RefundResponseDTO updatedRefund = refundRequestService.updateStatus(refundRequestId,
                                status);
                return ResponseEntity.ok(ApiResponse.success(
                                "Refund request status updated successfully",
                                updatedRefund,
                                httpRequest.getRequestURI()));
        }

        @PatchMapping("/{refundRequestId}/method")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RefundResponseDTO>> updateRefundMethod(
                        @PathVariable Long refundRequestId,
                        @RequestParam RefundMethodEnum refundMethod,
                        HttpServletRequest httpRequest) {
                RefundResponseDTO updatedRefund = refundRequestService.updateRefundMethod(refundRequestId,
                                refundMethod);
                return ResponseEntity.ok(ApiResponse.success(
                                "Refund method updated successfully",
                                updatedRefund,
                                httpRequest.getRequestURI()));
        }

        @PostMapping("/{refundRequestId}/payment")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<RefundResponseDTO>> processRefundPayment(
                        @PathVariable Long refundRequestId,
                        @RequestBody CreateRefundPaymentDTO createRefundPaymentDTO,
                        HttpServletRequest httpRequest) {
                RefundResponseDTO refundResponseDTO = refundRequestService.processRefundPayment(refundRequestId,
                                createRefundPaymentDTO);
                return ResponseEntity.ok(ApiResponse.success(
                                "Refund payment processed successfully",
                                refundResponseDTO,
                                httpRequest.getRequestURI()));

        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<Page<RefundSummaryDTO>>> getMyRefundRequests(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        HttpServletRequest httpRequest) {
                Pageable pageable = Pageable.ofSize(size).withPage(page - 1);
                Page<RefundSummaryDTO> refundRequests = refundRequestService.getByCustomerId(userDetails.getUserId(),
                                pageable);
                return ResponseEntity.ok(ApiResponse.success(
                                "My refund requests retrieved successfully",
                                refundRequests,
                                httpRequest.getRequestURI()));
        }

}
