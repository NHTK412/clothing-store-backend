package com.example.clothingstore.enums;

public enum PromotionConditionTypeEnum {
    MIN_QUANTITY(true),
    MIN_ORDER_AMOUNT(false), // nghĩa là: tổng giá trị đơn hàng phải đạt một mức tối thiểu nào đó để áp dụng
    // khuyến mãi, ví dụ: đơn hàng phải có tổng giá trị từ 500.000đ trở lên mới được
    // giảm giá

    // Sản phẩm cụ thể: nghĩa là: khuyến mãi chỉ áp dụng cho một số sản phẩm cụ thể,
    // ví dụ: giảm giá 20% cho áo thun nam
    PRODUCT_SPECIFIC(true);

    private final boolean requiresProductGroup;

    PromotionConditionTypeEnum(boolean requiresProductGroup) {
        this.requiresProductGroup = requiresProductGroup;
    }

    public boolean isRequiresProductGroup() {
        return requiresProductGroup;
    }
}
