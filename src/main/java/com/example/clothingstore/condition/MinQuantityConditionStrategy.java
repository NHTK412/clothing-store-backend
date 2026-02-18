package com.example.clothingstore.condition;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;

// Chiến lược điều kiện khuyến mãi: kiểm tra xem đơn hàng có đủ số lượng sản phẩm để áp dụng khuyến mãi hay không
@Component
public class MinQuantityConditionStrategy implements PromotionConditionStrategy {

    @Override
    public boolean isSatisfied(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value) {

        // Value sẽ chứa số lượng tối thiểu cần thiết để áp dụng khuyến mãi, ví dụ:
        // value.get("minQuantity") sẽ trả về số lượng tối thiểu

        List<Integer> productDetailIds = (List<Integer>) value.get("productDetailIds");

        if (productDetailIds == null || productDetailIds.isEmpty()) {
            return false; // Không có danh sách productDetailIds được cung cấp
        }

        Integer minQuantity = (Integer) value.get("minQuantity");

        if (minQuantity == null) {
            return false; // Không có số lượng tối thiểu được cung cấp
        }

        // Tính tổng số lượng của các sản phẩm trong đơn hàng có productDetailId nằm
        // trong danh sách
        int totalQuantity = orderPreviewDTO.getOrderDetails().stream()
                // Lọc các orderDetail có productDetailId nằm trong danh sách productDetailIds
                .filter(orderDetail -> productDetailIds.contains(orderDetail.getProductDetailId()))
                // Lấy số lượng của từng orderDetail
                .mapToInt(orderDetail -> orderDetail.getQuantity())
                // Tính tổng số lượng
                .sum();

        // Kiểm tra xem tổng số lượng có lớn hơn hoặc bằng số lượng tối thiểu hay không
        return totalQuantity >= minQuantity;

    }

    @Override
    public PromotionConditionTypeEnum getType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getType'");
        return PromotionConditionTypeEnum.MIN_QUANTITY;
    }
}
