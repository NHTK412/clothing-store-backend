package com.example.clothingstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.clothingstore.dto.address.AddressRequestDTO;
import com.example.clothingstore.dto.address.AddressResponseDTO;
import com.example.clothingstore.security.CustomerUserDetails;
import com.example.clothingstore.service.AddressService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// @PreAuthorize("hasRole('CUSTOMER')")
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

        // @Autowired
        // private ShippingAddressService shippingAddressService;

        private final AddressService shippingAddressService;

        @PreAuthorize("hasRole('CUSTOMER')")
        @GetMapping
        public ResponseEntity<ApiResponse<Page<AddressResponseDTO>>> getAddressesByCustomerId(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size,
                        HttpServletRequest request) {

                Integer customerId = userDetails.getUserId();

                Pageable pageable = PageRequest.of(page - 1, size);

                Page<AddressResponseDTO> addresses = shippingAddressService
                                .getAddressesByCustomerId(customerId, pageable);

                // return ResponseEntity.ok(new ApiResponse<>(true, null, addresses));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully get the customer's address list", addresses,
                                                request.getRequestURI()));
        }

        // @PreAuthorize("hasRole('CUSTOMER')")
        // @GetMapping("/all")
        // public ResponseEntity<ApiResponse<List<AddressResponseDTO>>>
        // getAllShippingAddresses(
        // @AuthenticationPrincipal CustomerUserDetails userDetails,
        // @RequestParam(defaultValue = "1") Integer page,
        // @RequestParam(defaultValue = "10") Integer size) {

        // Pageable pageable = PageRequest.of(page - 1, size);

        // Integer customerId = userDetails.getUserId();

        // List<AddressResponseDTO> shippingAddressResponseDTOs = shippingAddressService
        // .getAllShippingAddresses(customerId, pageable);

        // return ResponseEntity.ok(
        // ApiResponse.success("Successfully get all shipping addresses",
        // shippingAddressResponseDTOs));
        // }

        @PreAuthorize("hasRole('CUSTOMER')")
        @PostMapping
        public ResponseEntity<ApiResponse<AddressResponseDTO>> createShippingAddress(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @RequestBody AddressRequestDTO shippingAddressRequestDTO,
                        HttpServletRequest request) {

                // Integer customerId = 1;

                Integer customerId = userDetails.getUserId();

                AddressResponseDTO shippingAddressResponseDTO = shippingAddressService
                                .createShippingAddress(customerId, shippingAddressRequestDTO);

                return ResponseEntity.ok(ApiResponse.created("Successfully created shipping address",
                                shippingAddressResponseDTO, request.getRequestURI()));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @DeleteMapping("/{shippingAddressId}")
        public ResponseEntity<ApiResponse<AddressResponseDTO>> deleteShippingAddress(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @PathVariable Integer shippingAddressId,
                        HttpServletRequest request) {
                // Integer customerId = 1;

                Integer customerId = userDetails.getUserId();

                AddressResponseDTO shippingAddressResponseDTO = shippingAddressService
                                .deleteShippingAddress(customerId, shippingAddressId);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully deleted shipping address",
                                                shippingAddressResponseDTO, request.getRequestURI()));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @PutMapping("/{shippingAddressId}")
        public ResponseEntity<ApiResponse<AddressResponseDTO>> updateShippingAddress(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @PathVariable Integer shippingAddressId,
                        @RequestBody AddressRequestDTO shippingAddressRequestDTO,
                        HttpServletRequest request) {
                Integer customerId = userDetails.getUserId();

                AddressResponseDTO shippingAddressResponseDTO = shippingAddressService
                                .updateShippingAddress(customerId, shippingAddressId, shippingAddressRequestDTO);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully updated shipping address",
                                                shippingAddressResponseDTO, request.getRequestURI()));
        }
}
