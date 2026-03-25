package com.example.clothingstore.enums;

public enum PromotionTypeEnum {

    // Type chuẩn hóa sau khi xác định các case: Tự động, được phát và phải nhập

    AUTOMATIC, // Tự động áp dụng
    CONDITIONAL, // Được phát
    COUPON_CODE, // Nhập mã

}

/// Kiểu khuyến mãi này hiện tại sẽ có 3 loại:
// 1. áp dụng không điều kiện,
// 2. áp dụng có điều kiện,
// 3. áp dụng khi nhập mã khuyến mãi

/// Mục đích của việc này là để làm tính năng giảm giá cho sản phẩm ví dụ giá
/// gốc là 100k giảm 10% thì sẽ còn 90k, hoặc giảm 20k thì sẽ còn 80k, hoặc mua
/// 1 tặng 1 thì sẽ được tặng thêm 1 sản phẩm miễn phí khi mua 1 sản phẩm. Thì
/// loại là áp dụng không điều kiện sẽ là loại khuyến mãi mà khi khách hàng mua
/// sản phẩm thì sẽ tự động được áp dụng khuyến mãi mà không.