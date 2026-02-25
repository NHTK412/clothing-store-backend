package com.example.clothingstore.strategy.condition.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.ProductColor;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.PromotionGroup;
import com.example.clothingstore.repository.PromotionGroupRepository;
import com.example.clothingstore.strategy.condition.PromotionConditionStrategy;

import lombok.RequiredArgsConstructor;

// Chiến lược điều kiện khuyến mãi: kiểm tra xem đơn hàng có đủ số lượng sản phẩm để áp dụng khuyến mãi hay không
@Component
@RequiredArgsConstructor
public class MinQuantityConditionStrategy implements PromotionConditionStrategy {

    final private PromotionGroupRepository promotionGroupRepository;

    @Override
    public boolean isSatisfied(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value) {

        // Value sẽ chứa số lượng tối thiểu cần thiết để áp dụng khuyến mãi, ví dụ:
        // value.get("minQuantity") sẽ trả về số lượng tối thiểu

        // List<Integer> productDetailIds = (List<Integer>)
        // value.get("productDetailIds");

        Integer promotionGroupId = (Integer) value.get("promotionGroupId");

        // if (productDetailIds == null || productDetailIds.isEmpty()) {
        // return false; // Không có danh sách productDetailIds được cung cấp
        // }

        if (promotionGroupId == null) {
            return false; // Không có promotionGroupId được cung cấp
        }

        Optional<PromotionGroup> promotionGroupOpt = promotionGroupRepository.findById(promotionGroupId);

        if (promotionGroupOpt.isEmpty()) {
            return false; // Không tìm thấy PromotionGroup với ID đã cho
        }

        PromotionGroup promotionGroup = promotionGroupOpt.get();

        // List<Integer> products = promotionGroup.getProducts().stream()
        // .map(product -> product.getProductId())
        // .toList();

        List<Integer> productIds = List.of();

        for (Product product : promotionGroup.getProducts()) {
            for (ProductColor productColor : product.getProductColors()) {
                for (ProductDetail productDetail : productColor.getProductDetails()) {
                    productIds.add(productDetail.getDetailId());
                }

            }
        }

        Integer minQuantity = (Integer) value.get("minQuantity");

        if (minQuantity == null) {
            return false; // Không có số lượng tối thiểu được cung cấp
        }

        // Tính tổng số lượng của các sản phẩm trong đơn hàng có productDetailId nằm
        // trong danh sách
        int totalQuantity = orderPreviewDTO.getOrderDetails().stream()
                // Lọc các orderDetail có productDetailId nằm trong danh sách productDetailIds
                .filter(orderDetail -> productIds.contains(orderDetail.getProductDetailId()))
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
