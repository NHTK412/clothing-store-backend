package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.product.ProductRequestDTO;
import com.example.clothingstore.dto.product.ProductResponseDTO;
import com.example.clothingstore.dto.product.ProductSummaryDTO;
import com.example.clothingstore.dto.product.ProductUpdateDTO;
import com.example.clothingstore.dto.productcolor.ProductColorRequestDTO;
import com.example.clothingstore.dto.productcolor.ProductColorResponseDTO;
import com.example.clothingstore.dto.productdetail.ProductDetailRequestDTO;
import com.example.clothingstore.dto.productdetail.ProductDetailResponseDTO;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.service.ProductService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor

public class ProductController {

        private final ProductService productService;

        @GetMapping("/{productId}")
        public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductDetailById(@PathVariable Integer productId) {
                ProductResponseDTO productResponseDTO = productService.getProductDetailById(productId);

                return ResponseEntity
                                .ok(ApiResponse.success("Successfully get the product detail", productResponseDTO));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<ProductSummaryDTO>>> getAllProduct(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size,
                        @RequestParam(required = false) Integer categoryId) {

                Pageable pageable = PageRequest.of(page - 1, size);

                List<ProductSummaryDTO> productSummaryDTOs = productService.getAllProduct(categoryId, pageable);

                return ResponseEntity.ok(ApiResponse.success("Successfully get the product list", productSummaryDTOs));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponse<ProductSummaryDTO>> createProduct(
                        @Valid @RequestBody ProductRequestDTO productRequest) {
                return ResponseEntity
                                .ok(ApiResponse.success("Successfully created the product",
                                                productService.createProduct(productRequest)));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{productId}")
        public ResponseEntity<ApiResponse<ProductSummaryDTO>> deleteProduct(@PathVariable Integer productId) {
                ProductSummaryDTO productSummaryDTO = productService.deleteProduct(productId);

                return ResponseEntity.ok(ApiResponse.success("Successfully deleted the product", productSummaryDTO));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{productId}")
        public ResponseEntity<ApiResponse<ProductSummaryDTO>> updateProduct(@PathVariable Integer productId,
                        @Valid @RequestBody ProductUpdateDTO productUpdate) {
                return ResponseEntity
                                .ok(ApiResponse.success("Successfully updated the product",
                                                productService.updateProduct(productId, productUpdate)));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/product-color/{productColorId}")
        public ResponseEntity<ApiResponse<ProductColorResponseDTO>> updateProductColor(
                        @PathVariable Integer productColorId,
                        @RequestBody ProductColorRequestDTO productColorRequest) {

                return ResponseEntity
                                .ok(ApiResponse.success("Successfully updated the product color",
                                                productService.updateProductColor(productColorId,
                                                                productColorRequest)));

        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/product-color/{productColorId}")
        public ResponseEntity<ApiResponse<ProductColorResponseDTO>> deleteProductColor(
                        @PathVariable Integer productColorId) {
                ProductColorResponseDTO productColorResponseDTO = productService.deleteProductColor(productColorId);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully deleted the product color", productColorResponseDTO));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/product-color")
        public ResponseEntity<ApiResponse<ProductColorResponseDTO>> createProductColor(
                        @RequestBody ProductColorRequestDTO productColorRequest) {

                return ResponseEntity
                                .ok(ApiResponse.created("Successfully created the product color",
                                                productService.createProductColor(productColorRequest)));

        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/product-color/{productColorId}/product-detail")
        public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> createProductDetail(
                        @RequestBody ProductDetailRequestDTO productDetailRequest,
                        @PathVariable Integer productColorId) {

                return ResponseEntity
                                .ok(ApiResponse.created("Successfully created the product detail",
                                                productService.createProductDetail(productColorId,
                                                                productDetailRequest)));

        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/product-color/{productColorId}/product-detail/{productDetailId}")
        public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> updateProductDetail(
                        @PathVariable Integer productColorId,
                        @PathVariable Integer productDetailId,
                        @RequestBody ProductDetailRequestDTO productDetailRequest) {

                return ResponseEntity
                                .ok(ApiResponse.success("Successfully updated the product detail",
                                                productService.updateProductDetail(productColorId, productDetailId,
                                                                productDetailRequest)));

        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/product-color/{productColorId}/product-detail/{productDetailId}")
        public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> deleteProductDetail(
                        @PathVariable Integer productColorId,
                        @PathVariable Integer productDetailId) {

                ProductDetailResponseDTO productDetailResponseDTO = productService.deleteProductDetail(productColorId,
                                productDetailId);

                return ResponseEntity.ok(ApiResponse.success("Successfully deleted the product detail",
                                productDetailResponseDTO));
        }

}
