package com.example.clothingstore.dto.gift;

import com.example.clothingstore.dto.product.ProductDetailPromotionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftResponseDTO {

    private Integer giftId;

    private Integer giftQuantity;

    private Integer maxGift;

    private Integer productDetailId;

    private Double unitPrice;

    private String productImage;

    private String productColor;

    private String productSize;

    private Integer productQuantity;

    // private ProductDetailPromotionDTO productDetailPromotionDTO;

}
