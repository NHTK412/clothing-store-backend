package com.example.clothingstore.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.promotion.PromotionCreateRequestDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.enums.PromotionApplicationTypeEnum;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;
import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.business.BadRequestException;

@Component
public class PromotionValidator {

    public void validatePromotionRequest(PromotionCreateRequestDTO requestDTO) {

        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (requestDTO.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }

        if (requestDTO.getPromotionType() == PromotionTypeEnum.COUPON_CODE) {
            // if (requestDTO.getCouponCode() == null ||
            // requestDTO.getCouponCode().isBlank()) {
            // throw new BadRequestException("Coupon code is required for COUPON_CODE
            // type");
            // }

            if (requestDTO.getCouponCode() == null || requestDTO.getCouponCode().isBlank()) {
                requestDTO.setCouponCode(generateRandomCouponCode());
            }

            if (requestDTO.getUsageLimit() == null || requestDTO.getUsageLimit() <= 0) {
                throw new BadRequestException("Usage limit must be positive for COUPON_CODE type");
            }
        }

        if (requestDTO.getPromotionScopeType() == PromotionScopeTypeEnum.SPECIFIC_USER) {
            if (requestDTO.getTargetUserIds() == null || requestDTO.getTargetUserIds().isEmpty()) {
                throw new BadRequestException("Target user IDs are required for SPECIFIC_USER scope");
            }
        } else if (requestDTO.getPromotionScopeType() == PromotionScopeTypeEnum.MEMBER_RANK) {
            if (requestDTO.getTargetMemberTierIds() == null || requestDTO.getTargetMemberTierIds().isEmpty()) {
                throw new BadRequestException("Target member tier IDs are required for MEMBER_RANK scope");
            }
        }

        if (requestDTO.getConditions() == null || requestDTO.getConditions().isEmpty()) {
            throw new BadRequestException("At least one condition is required");
        }

        if (requestDTO.getActions() == null || requestDTO.getActions().isEmpty()) {
            throw new BadRequestException("At least one action is required");
        }

        validateApplicationTypeLogic(requestDTO);
    }

    private void validateApplicationTypeLogic(PromotionCreateRequestDTO requestDTO) {

        PromotionApplicationTypeEnum appType = requestDTO.getApplicationType();

        if (appType == null) {
            throw new BadRequestException("Application type is required");
        }

        if (appType == PromotionApplicationTypeEnum.PRODUCT_LEVEL) {
            validateProductLevelPromotion(requestDTO);
        } else if (appType == PromotionApplicationTypeEnum.ORDER_LEVEL) {
            validateOrderLevelPromotion(requestDTO);
        }
    }

    private void validateProductLevelPromotion(PromotionCreateRequestDTO requestDTO) {

        if (requestDTO.getPromotionGroupIds() == null || requestDTO.getPromotionGroupIds().isEmpty()) {
            throw new BadRequestException("At least one promotion group is required");
        }

        boolean allProductSpecific = requestDTO.getConditions().stream()
                .allMatch(c -> c.getConditionType() == PromotionConditionTypeEnum.PRODUCT_SPECIFIC);

        if (!allProductSpecific) {
            throw new BadRequestException(
                    "PRODUCT_LEVEL promotion must have ALL conditions as PRODUCT_SPECIFIC");
        }

        boolean allProductDiscount = requestDTO.getActions().stream()
                .allMatch(a -> a.getActionType() == PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT
                        || a.getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT);

        if (!allProductDiscount) {
            throw new BadRequestException(
                    "PRODUCT_LEVEL promotion must have ALL actions as PRODUCT_PERCENT_DISCOUNT or PRODUCT_FIXED_DISCOUNT");
        }

        if (requestDTO.getPromotionType() != PromotionTypeEnum.AUTOMATIC) {
            throw new BadRequestException(
                    "PRODUCT_LEVEL promotion must be AUTOMATIC type");
        }
    }

    private void validateOrderLevelPromotion(PromotionCreateRequestDTO requestDTO) {

        // boolean hasProductSpecificCondition = requestDTO.getConditions().stream()
        // .anyMatch((condition) -> condition.getConditionType() ==
        // PromotionConditionTypeEnum.PRODUCT_SPECIFIC
        // || condition.getConditionType() == PromotionConditionTypeEnum.MIN_QUANTITY);

        boolean hasProductSpecificCondition = requestDTO.getConditions().stream()
                .anyMatch((condition) -> condition.getConditionType().isRequiresProductGroup());

        // boolean hasProductDiscountAction = requestDTO.getActions().stream()
        // .anyMatch((action) -> action.getActionType() ==
        // PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT
        // || action.getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT);

        boolean hasProductDiscountAction = requestDTO.getActions().stream()
                .anyMatch((action) -> action.getActionType().isRequiresProductGroup());

        // boolean hasProductGroup = requestDTO.getPromotionGroups() != null &&
        // !requestDTO.getPromotionGroups().isEmpty();

        boolean hasProductGroup = requestDTO.getPromotionGroupIds() != null
                && !requestDTO.getPromotionGroupIds().isEmpty();

        // Nếu có điều kiện hoặc hành động liên quan đến sản phẩm nhưng không có nhóm
        // khuyến mãi nào, thì lỗi
        if ((hasProductSpecificCondition || hasProductDiscountAction) && !hasProductGroup) {
            throw new BadRequestException(
                    "ORDER_LEVEL promotion with product-specific conditions or actions must have at least one promotion group");
        }

    }

    private String generateRandomCouponCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder couponCode = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = (int) (Math.random() * chars.length()); // 0 to chars.length() - 1
            couponCode.append(chars.charAt(randomIndex));
        }
        return couponCode.toString();
    }
}