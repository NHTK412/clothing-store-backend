package com.example.clothingstore.dto.promotion;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.Map;

import com.example.clothingstore.enums.PromotionApplicationTypeEnum;
import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionCreateRequestDTO {

    @NotBlank(message = "Promotion name is required")
    private String promotionName;

    private String description;

    @NotNull(message = "Promotion type is required")
    private PromotionTypeEnum promotionType; // AUTOMATIC, CONDITIONAL, COUPON_CODE

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    // @Positive(message = "Priority must be greater than 0")
    // private Integer priority;

    // private Boolean stackable = false; // Mặc định không stackable

    private Boolean stackable = true; // Mặc định cho phép stackable, chỉ không stackable khi promotionType là
                                      // AUTOMATIC

    // Nếu loại là COUPON_CODE
    private String couponCode;

    @Positive(message = "Usage limit must be greater than 0")
    private Integer usageLimit;

    @NotNull(message = "Application type is required")
    private PromotionApplicationTypeEnum applicationType;

    @NotNull(message = "Promotion scope type is required")
    private PromotionScopeTypeEnum promotionScopeType;

    // Danh sách điều kiện áp dụng
    @Valid
    private List<PromotionConditionRequestDTO> conditions;

    // Danh sách hành động khuyến mãi
    @Valid
    private List<PromotionActionRequestDTO> actions;

    private List<Integer> promotionGroupIds; // Nếu áp dụng cho nhóm sản phẩm cụ thể

    // Nếu SPECIFIC_USER hoặc MEMBER_RANK, cung cấp danh sách user/tier
    private List<Integer> targetUserIds; // Cho SPECIFIC_USER

    private List<Integer> targetMemberTierIds; // Cho MEMBER_RANK
}