package com.example.clothingstore.action;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.repository.ProductDetailRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FreeProductStrategy implements PromotionActionStrategy {

    final private ProductDetailRepository productDetailRepository;

    @Override
    public void execute(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value) {

        // List<Integer> freeProductIds = (List<Integer>) value.get("freeProductIds");

        Integer freeProductId = (Integer) value.get("freeProductId");

        Integer quantity = (Integer) value.get("quantity");

        ProductDetail freeProductDetail = productDetailRepository.findById(freeProductId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + freeProductId));

        OrderDetailPreviewDTO freeOrderDetail = OrderDetailPreviewDTO.builder()
                .productDetailId(freeProductDetail.getDetailId())
                .productName(freeProductDetail.getProductColor().getProduct().getProductName())
                .productImage(freeProductDetail.getProductColor().getProductImage())
                .color(freeProductDetail.getProductColor().getColor())
                .size(freeProductDetail.getSize())
                .quantity(quantity)
                .price(freeProductDetail.getProductColor().getProduct().getUnitPrice())
                .discountAmount(freeProductDetail.getProductColor().getProduct().getUnitPrice())
                .finalPrice(0.0)
                .isFree(true)
                .build();

        orderPreviewDTO.getOrderDetails().add(freeOrderDetail);

    }

    @Override
    public PromotionActionTypeEnum getType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getType'");
        return PromotionActionTypeEnum.FREE_PRODUCT;
    }

}
