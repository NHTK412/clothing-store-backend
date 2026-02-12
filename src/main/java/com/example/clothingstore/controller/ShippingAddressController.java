package com.example.clothingstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.clothingstore.dto.shippingaddress.ShippingAddressRequestDTO;
import com.example.clothingstore.dto.shippingaddress.ShippingAddressResponseDTO;
import com.example.clothingstore.security.CustomerUserDetails;
import com.example.clothingstore.service.ShippingAddressService;
import com.example.clothingstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;

// @PreAuthorize("hasRole('CUSTOMER')")
@RestController
@RequestMapping("/shipping-address")
@RequiredArgsConstructor
public class ShippingAddressController {

        // @Autowired
        // private ShippingAddressService shippingAddressService;

        private final ShippingAddressService shippingAddressService;

        @PreAuthorize("hasRole('CUSTOMER')")
        @GetMapping
        public ResponseEntity<ApiResponse<List<ShippingAddressResponseDTO>>> getAddressesByCustomerId(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size) {

                Integer customerId = userDetails.getUserId();

                Pageable pageable = PageRequest.of(page - 1, size);

                List<ShippingAddressResponseDTO> addresses = shippingAddressService
                                .getAddressesByCustomerId(customerId, pageable);

                return ResponseEntity.ok(new ApiResponse<>(true, null, addresses));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @GetMapping("/all")
        public ResponseEntity<ApiResponse<List<ShippingAddressResponseDTO>>> getAllShippingAddresses(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size) {

                Pageable pageable = PageRequest.of(page - 1, size);

                Integer customerId = userDetails.getUserId();

                List<ShippingAddressResponseDTO> shippingAddressResponseDTOs = shippingAddressService
                                .getAllShippingAddresses(customerId, pageable);

                return ResponseEntity.ok(
                                new ApiResponse<List<ShippingAddressResponseDTO>>(true, null,
                                                shippingAddressResponseDTOs));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @PostMapping
        public ResponseEntity<ApiResponse<ShippingAddressResponseDTO>> createShippingAddress(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestBody ShippingAddressRequestDTO shippingAddressRequestDTO) {

                // Integer customerId = 1;

                Integer customerId = userDetails.getUserId();

                ShippingAddressResponseDTO shippingAddressResponseDTO = shippingAddressService
                                .createShippingAddress(customerId, shippingAddressRequestDTO);

                return ResponseEntity.ok(
                                new ApiResponse<ShippingAddressResponseDTO>(true, null, shippingAddressResponseDTO));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @DeleteMapping("/{shippingAddressId}")
        public ResponseEntity<ApiResponse<ShippingAddressResponseDTO>> deleteShippingAddress(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @PathVariable Integer shippingAddressId) {
                // Integer customerId = 1;

                Integer customerId = userDetails.getUserId();

                ShippingAddressResponseDTO shippingAddressResponseDTO = shippingAddressService
                                .deleteShippingAddress(customerId, shippingAddressId);

                return ResponseEntity.ok(
                                new ApiResponse<ShippingAddressResponseDTO>(true, null, shippingAddressResponseDTO));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @PutMapping("/{shippingAddressId}")
        public ResponseEntity<ApiResponse<ShippingAddressResponseDTO>> updateShippingAddress(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @PathVariable Integer shippingAddressId,
                        @RequestBody ShippingAddressRequestDTO shippingAddressRequestDTO) {
                Integer customerId = userDetails.getUserId();

                ShippingAddressResponseDTO shippingAddressResponseDTO = shippingAddressService
                                .updateShippingAddress(customerId, shippingAddressId, shippingAddressRequestDTO);

                return ResponseEntity.ok(
                                new ApiResponse<ShippingAddressResponseDTO>(true, null, shippingAddressResponseDTO));
        }
}
