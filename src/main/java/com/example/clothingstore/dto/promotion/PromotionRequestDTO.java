package com.example.clothingstore.dto.promotion;

import java.time.LocalDateTime;
import java.util.List;

import com.example.clothingstore.dto.discount.DiscountRequestDTO;
import com.example.clothingstore.dto.gift.GiftRequestDTO;
import com.example.clothingstore.dto.promotiongroup.PromotionGroupRequestDTO;
import com.example.clothingstore.enums.PromotionTypeEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequestDTO {

    @NotBlank(message = "Promotion name cannot be blank")
    private String promotionName;

    @NotNull(message = "Promotion type is required")
    private PromotionTypeEnum promotionType;

    private String description;

    // private String applyCondition;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    // private String applyType;

    @Valid
    @NotEmpty(message = "Promotion groups list cannot be empty")
    private List<PromotionGroupRequestDTO> promotionGroupRequestDTOs; // Danh sách các nhóm khuyến mãi áp dụng cho chương trình khuyến mãi này

    @Valid
    private DiscountRequestDTO discountRequestDTO; // Thông tin chi tiết về giảm giá (nếu có)

    @Valid
    private List<GiftRequestDTO> giftRequestDTOs; // Thông tin chi tiết về quà tặng (nếu có)
}
