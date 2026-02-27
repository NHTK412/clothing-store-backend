package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.customer.CustomerRequestDTO;
import com.example.clothingstore.dto.customer.CustomerResponseDTO;
import com.example.clothingstore.dto.customer.CustomerSummaryDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.security.CustomerUserDetails;
import com.example.clothingstore.service.CustomerService;
import com.example.clothingstore.service.OrderService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
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

        // @Autowired
        // private CustomerService customerService;

        private final CustomerService customerService;

        // private final OrderService orderService;

        @PreAuthorize("hasRole('CUSTOMER')")
        @PutMapping("/me")
        public ResponseEntity<ApiResponse<CustomerResponseDTO>> updateMe(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {

                // BCryptPasswordEncoder bCryptPasswordEncoder = BCryptPasswordEncoder();

                Integer customerId = userDetails.getUserId();

                CustomerResponseDTO customerResponseDTO = customerService.updateCustomer(customerId,
                                customerRequestDTO);

                // return ResponseEntity.ok(new ApiResponse<CustomerResponseDTO>(true, null,
                // customerResponseDTO));

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully updated customer", customerResponseDTO));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<CustomerResponseDTO>> getMe(
                        @AuthenticationPrincipal CustomerUserDetails userDetails) {

                Integer customerId = userDetails.getUserId();

                CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(customerId);

                // return ResponseEntity.ok(new ApiResponse<CustomerResponseDTO>(true, null,
                // customerResponseDTO));

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved customer information",
                                                customerResponseDTO));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/{customerId}")
        public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerByID(@PathVariable Integer customerId) {

                CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(customerId);

                // return ResponseEntity.ok(new ApiResponse<CustomerResponseDTO>(true, null,
                // customerResponseDTO));

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved customer information",
                                                customerResponseDTO));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<ApiResponse<Page<CustomerSummaryDTO>>> getAllCustomer(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size) {
                Pageable pageable = PageRequest.of(page - 1, size);

                Page<CustomerSummaryDTO> customerSummaryDTO = customerService.getAllCustomer(pageable);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved customers", customerSummaryDTO));
        }

        // Xem lại admin k tự tạo customer
        // ======================================================================================================================
        // @PreAuthorize("hasRole('ADMIN')")
        // @PostMapping
        // public ResponseEntity<ApiResponse<CustomerResponseDTO>> createCustomer(
        // @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {

        // CustomerResponseDTO customerResponseDTO =
        // customerService.createCustomer(customerRequestDTO);

        // // return ResponseEntity.ok(new ApiResponse<CustomerResponseDTO>(true, null,
        // // customerResponseDTO));
        // return ResponseEntity.ok(
        // ApiResponse.created("Successfully created customer", customerResponseDTO));
        // }
        // ======================================================================================================================

        // @PreAuthorize("hasRole('ADMIN')")
        // @PutMapping("/{customerId}") // dành cho admin
        // public ResponseEntity<ApiResponse<CustomerResponseDTO>>
        // updateCustomer(@PathVariable Integer customerId,
        // @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {

        // // BCryptPasswordEncoder bCryptPasswordEncoder = BCryptPasswordEncoder();

        // CustomerResponseDTO customerResponseDTO =
        // customerService.updateCustomer(customerId, customerRequestDTO);

        // return ResponseEntity.ok(
        // ApiResponse.success("Successfully updated customer", customerResponseDTO));
        // }

        // @PreAuthorize("hasRole('ADMIN')")
        // @DeleteMapping("/{customerId}")
        // public ResponseEntity<ApiResponse<CustomerResponseDTO>>
        // deleteCustomer(@PathVariable Integer customerId) {

        // CustomerResponseDTO customerResponseDTO =
        // customerService.deleteCustomer(customerId);

        // // return ResponseEntity.ok(new ApiResponse<CustomerResponseDTO>(true, null,
        // // customerResponseDTO));

        // return ResponseEntity.ok(
        // ApiResponse.success("Successfully deleted customer", customerResponseDTO));
        // }

        // Chưa xử lý search
        // @PreAuthorize("hasRole('CUSTOMER')")
        // @GetMapping("/me/orders")
        // public ResponseEntity<ApiResponse<List<OrderSummaryDTO>>>
        // getAllOrderByCustomer(
        // @RequestParam(defaultValue = "1") Integer page,
        // @RequestParam(defaultValue = "10") Integer size,
        // @RequestParam(required = false) String search,
        // @AuthenticationPrincipal CustomerUserDetails userDetails) {
        // Pageable pageable = PageRequest.of(page - 1, size);
        // Integer customerId = userDetails.getUserId();
        // List<OrderSummaryDTO> orderSummaries =
        // orderService.getAllOrdersByCustomer(customerId, pageable);
        // return ResponseEntity.ok(
        // ApiResponse.success("Successfully retrieved orders", orderSummaries));
        // }

}
