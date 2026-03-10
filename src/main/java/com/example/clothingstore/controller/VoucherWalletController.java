package com.example.clothingstore.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.promotion.PromotionSummaryDTO;
import com.example.clothingstore.service.VoucherWalletService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/customer/me/vouchers")
@RequiredArgsConstructor
public class VoucherWalletController {

        final private VoucherWalletService voucherWalletService;

        // Xem các voucher trong ví của khách hàng

        @GetMapping
        public ResponseEntity<ApiResponse<List<PromotionSummaryDTO>>> getVouchersForCustomer(
                        @AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        HttpServletRequest request) {

                Integer customerId = customerUserDetails.getUserId();

                Pageable pageable = PageRequest.of(page - 1, size);

                List<PromotionSummaryDTO> promotionSummaryDTOs = voucherWalletService.getVouchersForCustomer(customerId,
                                pageable);

                // return ResponseEntity.ok(new ApiResponse<List<PromotionSummaryDTO>>(true,
                // "Success", promotionSummaryDTOs));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved vouchers in wallet", promotionSummaryDTOs,
                                                request.getRequestURI()));
        }

        // Thêm voucher vào ví của khách hàng
        @PostMapping
        public ResponseEntity<ApiResponse<PromotionSummaryDTO>> addPromotionToWallet(
                        @AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                        @RequestParam String couponCode,
                        HttpServletRequest request) {

                Integer customerId = customerUserDetails.getUserId();

                PromotionSummaryDTO promotionSummaryDTO = voucherWalletService.addPromotionToWallet(customerId,
                                couponCode);

                // return ResponseEntity.ok(new ApiResponse<PromotionSummaryDTO>(true,
                // "Promotion added to wallet successfully",
                // promotionSummaryDTO));

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully added promotion to wallet", promotionSummaryDTO,
                                                request.getRequestURI()));
        }

}
