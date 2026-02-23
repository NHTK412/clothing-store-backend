// package com.example.clothingstore.service;

// import java.time.LocalDateTime;

// import org.springframework.stereotype.Component;

// import com.example.clothingstore.dto.promotion.PromotionCreateRequestDTO;
// import com.example.clothingstore.enums.PromotionScopeTypeEnum;
// import com.example.clothingstore.enums.PromotionTypeEnum;
// import com.example.clothingstore.exception.customer.BadRequestException;

// @Component
// public class PromotionValidator {

//     public void validatePromotionRequest(PromotionCreateRequestDTO requestDTO) {

//         // Validate dates
//         if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
//             throw new BadRequestException("Start date must be before end date");
//         }

//         if (requestDTO.getStartDate().isBefore(LocalDateTime.now())) {
//             throw new BadRequestException("Start date cannot be in the past");
//         }

//         // Validate COUPON_CODE type
//         if (requestDTO.getPromotionType() == PromotionTypeEnum.COUPON_CODE) {
//             if (requestDTO.getCouponCode() == null || requestDTO.getCouponCode().isBlank()) {
//                 throw new BadRequestException("Coupon code is required for COUPON_CODE type");
//             }
//             if (requestDTO.getUsageLimit() == null || requestDTO.getUsageLimit() <= 0) {
//                 throw new BadRequestException("Usage limit must be positive for COUPON_CODE type");
//             }
//         }

//         // Validate scope type
//         if (requestDTO.getPromotionScopeType() == PromotionScopeTypeEnum.SPECIFIC_USER) {
//             if (requestDTO.getTargetUserIds() == null || requestDTO.getTargetUserIds().isEmpty()) {
//                 throw new BadRequestException("Target user IDs are required for SPECIFIC_USER scope");
//             }
//         } else if (requestDTO.getPromotionScopeType() == PromotionScopeTypeEnum.MEMBER_RANK) {
//             if (requestDTO.getTargetMemberTierIds() == null || requestDTO.getTargetMemberTierIds().isEmpty()) {
//                 throw new BadRequestException("Target member tier IDs are required for MEMBER_RANK scope");
//             }
//         }

//         // Validate conditions and actions
//         if (requestDTO.getConditions() == null || requestDTO.getConditions().isEmpty()) {
//             throw new BadRequestException("At least one condition is required");
//         }

//         if (requestDTO.getActions() == null || requestDTO.getActions().isEmpty()) {
//             throw new BadRequestException("At least one action is required");
//         }

//         if (requestDTO.getPromotionGroups() == null || requestDTO.getPromotionGroups().isEmpty()) {
//             throw new BadRequestException("At least one promotion group is required");
//         }
//     }
// }