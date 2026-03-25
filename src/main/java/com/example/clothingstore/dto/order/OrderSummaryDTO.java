package com.example.clothingstore.dto.order;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    private Integer orderId; // Nghĩa tiếng việt: Mã đơn hàng

    private Double finalAmount; // Nghĩa tiếng việt: Tổng tiền hàng

    // private Double discountAmount; // Nghĩa tiếng việt: Số tiền được giảm giá

    private Double shippingFee; // Nghĩa tiếng việt: Phí vận chuyển

    private LocalDateTime deliveryDate; // Nghĩa tiếng việt: Ngày giao hàng

    private OrderStatusEnum status; // Nghĩa tiếng việt: Trạng thái đơn hàng

    private String orderFirstName;

    private String orderFirstImage;

    private Integer orderQuantity;

    private Boolean isReview;

    // private String recipientName; // Nghĩa tiếng việt: Tên người nhận

    // private String phoneNumber; // Nghĩa tiếng việt: Số điện thoại người nhận

    // private String detailedAddress; // Nghĩa tiếng việt: Địa chỉ chi tiết

    // private String ward; // Nghĩa tiếng việt: Phường/Xã

    // private String province; // Nghĩa tiếng việt: Tỉnh/Thành phố

    // private OrderPaymentStatusEnum paymentStatus; // Nghĩa tiếng việt: Trạng thái
    // thanh toán
}
