package com.example.clothingstore.enums;

public enum PromotionActionTypeEnum {
    PERCENT_DISCOUNT(false), // nghĩa là: giảm giá theo phần trăm, ví dụ: giảm 10% cho đơn hàng
    FREE_PRODUCT(false), // nghĩa là: tặng sản phẩm miễn phí, ví dụ: mua 1 tặng 1, hoặc mua 2 tặng 1
    FIXED_DISCOUNT(false), // nghĩa là: giảm giá theo số tiền cố định, ví dụ: giảm 50.000đ cho đơn hàng

    // Giảm giá cho sản phẩm
    PRODUCT_PERCENT_DISCOUNT(true), // nghĩa là: giảm giá cho một sản phẩm cụ thể, ví dụ: giảm 20% cho áo thun nam
    // Số tiền cụ thể
    PRODUCT_FIXED_DISCOUNT(true), // nghĩa là: giảm giá theo số tiền cố định, ví dụ: giảm 50.000đ cho áo thun nam

    FREE_SHIP(false);

    private final boolean requiresProductGroup;

    PromotionActionTypeEnum(boolean requiresProductGroup) {
        this.requiresProductGroup = requiresProductGroup;
    }

    public boolean isRequiresProductGroup() {
        return requiresProductGroup;
    }

}
