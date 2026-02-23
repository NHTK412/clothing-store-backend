package com.example.clothingstore.dto.order;

import java.util.List;
import java.util.Set;

import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderPreviewDTO {

    // public static final String OrderDetailPreviewDTO = null;

    private Set<OrderDetailPreviewDTO> orderDetails;

    private Double totalAmount;

    private Double discountAmount; // Tổng số tiền giảm giá từ các chiến lược khuyến mãi áp dụng

    private Double shippingFee; // Phí vận chuyển

    private Double discountShippingFee; // Số tiền giảm giá cho phí vận chuyển

    private Double finalAmount;

    private List<Integer> appliedPromotions; // Danh sách tên các chiến lược khuyến mãi đã áp dụng

}
