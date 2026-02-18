// package com.example.clothingstore.controller;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.clothingstore.dto.cart.CartCheckPromotionDTO;
// import com.example.clothingstore.dto.promotion.PromotionRequestDTO;
// import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
// import com.example.clothingstore.dto.promotion.PromotionSummaryDTO;
// import com.example.clothingstore.enums.PromotionTypeEnum;
// import com.example.clothingstore.model.Promotion;
// import com.example.clothingstore.service.PromotionService;
// import com.example.clothingstore.util.ApiResponse;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;

// @RestController
// @RequestMapping("/admin/promotion")
// @RequiredArgsConstructor
// public class PromotionController {

//     // @Autowired
//     // private PromotionService promotionService;

//     private final PromotionService promotionService;

//     @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
//     @GetMapping("/{promotionId}")
//     public ResponseEntity<ApiResponse<PromotionResponseDTO>> getPromotionById(@PathVariable Integer promotionId) {

//         PromotionResponseDTO promotionResponseDTO = promotionService.getPromotionById(promotionId);

//         return ResponseEntity.ok(new ApiResponse<PromotionResponseDTO>(true, null, promotionResponseDTO));
//     }

//     @PreAuthorize("hasAnyRole('ADMIN')")
//     @PostMapping
//     public ResponseEntity<ApiResponse<PromotionResponseDTO>> createPromotion(
//             @Valid @RequestBody PromotionRequestDTO promotionRequestDTO) {

//         PromotionResponseDTO promotionResponseDTO = promotionService.createPromotion(promotionRequestDTO);

//         // PromotionResponseDTO promotionResponseDTO = null;

//         return ResponseEntity.ok(new ApiResponse<PromotionResponseDTO>(true, null, promotionResponseDTO));
//     }

//     // Kiểm tra mã khuyến mãi nào áp dụng được cho đơn hàng này (loại khuyến mãi
//     // giảm giá)
//     @PreAuthorize("hasAnyRole('CUSTOMER')")
//     @PostMapping("/applicable-discount")
//     public ResponseEntity<ApiResponse<List<PromotionSummaryDTO>>> getApplicableDiscountPromotion(
//             @Valid @RequestBody CartCheckPromotionDTO cartCheckPromotionDTO) {
//         List<PromotionSummaryDTO> promotionSummaryDTOs = promotionService
//                 .getApplicableDiscountPromotion(cartCheckPromotionDTO);

//         ApiResponse<List<PromotionSummaryDTO>> apiResponse = new ApiResponse<List<PromotionSummaryDTO>>(true, null,
//                 promotionSummaryDTOs);

//         return ResponseEntity.ok(apiResponse);

//     }

//     @PreAuthorize("hasAnyRole('CUSTOMER')")
//     @PostMapping("/applicable")
//     public ResponseEntity<ApiResponse<List<PromotionSummaryDTO>>> getApplicablePromotion(
//             @Valid @RequestBody CartCheckPromotionDTO cartCheckPromotionDTO,
//             @RequestParam List<PromotionTypeEnum> promotionTypes) {
//         List<PromotionSummaryDTO> promotionSummaryDTOs = promotionService
//                 .getApplicablePromotion(cartCheckPromotionDTO, promotionTypes);

//         ApiResponse<List<PromotionSummaryDTO>> apiResponse = new ApiResponse<List<PromotionSummaryDTO>>(true, null,
//                 promotionSummaryDTOs);

//         return ResponseEntity.ok(apiResponse);

//     }

//     @PreAuthorize("hasAnyRole('ADMIN')")
//     @GetMapping
//     public ResponseEntity<ApiResponse<List<PromotionSummaryDTO>>> getAllPromotions(
//             @RequestParam(defaultValue = "1") Integer page,
//             @RequestParam(defaultValue = "10") Integer size) {

//         PageRequest pageable = PageRequest.of(page, size);

//         List<PromotionSummaryDTO> promotionSummaryDTOs = promotionService.getAllPromotions(pageable);

//         ApiResponse<List<PromotionSummaryDTO>> apiResponse = new ApiResponse<List<PromotionSummaryDTO>>(true, null,
//                 promotionSummaryDTOs);

//         return ResponseEntity.ok(apiResponse);

//     }

//     @PreAuthorize("hasAnyRole('ADMIN')")
//     @PatchMapping("/{promotionId}")
//     public ResponseEntity<ApiResponse<PromotionResponseDTO>> deletePromotion(@PathVariable Integer promotionId) {

//         PromotionResponseDTO promotionResponseDTO = promotionService.deletePromotion(promotionId);

//         return ResponseEntity.ok(new ApiResponse<PromotionResponseDTO>(true, null, promotionResponseDTO));
//     }

//     // @PatchMapping("/{promotionId}")
//     // public ResponseEntity<ApiResponse<PromotionResponseDTO>>
//     // deletePromotion(@PathVariable Integer promotionId, @RequestParam
//     // PromotionStatusEnum status) {

//     // promotionService.deletePromotion(promotionId);

//     // return ResponseEntity.ok(new ApiResponse<PromotionResponseDTO>(true,
//     // "Promotion deleted successfully", null));
//     // }

// }


