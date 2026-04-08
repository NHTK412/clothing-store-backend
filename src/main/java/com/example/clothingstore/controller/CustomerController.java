package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.customer.CustomerRequestDTO;
import com.example.clothingstore.dto.customer.CustomerResponseDTO;
import com.example.clothingstore.dto.customer.CustomerSummaryDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.service.CustomerService;
import com.example.clothingstore.service.OrderService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("v1/customers")
@RequiredArgsConstructor
public class CustomerController {


        private final CustomerService customerService;

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<ApiResponse<Page<CustomerSummaryDTO>>> getAllCustomer(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size,
                        HttpServletRequest request) {
                Pageable pageable = PageRequest.of(page - 1, size);

                Page<CustomerSummaryDTO> customerSummaryDTO = customerService.getAllCustomer(pageable);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved customers", customerSummaryDTO,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{customerId}")
        public ResponseEntity<ApiResponse<CustomerResponseDTO>> deleteCustomer(@PathVariable Integer customerId,
                        HttpServletRequest request) {

                CustomerResponseDTO customerResponseDTO = customerService.deleteCustomer(customerId);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully deleted customer", customerResponseDTO,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/{customerId}")
        public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(@PathVariable Integer customerId,
                        HttpServletRequest request) {

                CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(customerId);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved customer information",
                                                customerResponseDTO, request.getRequestURI()));
        }

}
