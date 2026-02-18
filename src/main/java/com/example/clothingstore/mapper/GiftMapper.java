// package com.example.clothingstore.mapper;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.example.clothingstore.dto.gift.GiftRequestDTO;
// import com.example.clothingstore.dto.gift.GiftResponseDTO;
// import com.example.clothingstore.model.Gift;

// @Component
// public class GiftMapper {

//     // @Autowired
//     // private ProductDetailMapper productDetailMapper;

//     public GiftResponseDTO convertModelToDiscountResponseDTO(Gift gift) {
//         GiftResponseDTO discountResponseDTO = new GiftResponseDTO();

//         discountResponseDTO.setGiftId(gift.getGiftId());

//         discountResponseDTO.setGiftQuantity(gift.getGiftQuantity());

//         discountResponseDTO.setMaxGift(gift.getMaxGift());

//         // discountResponseDTO.setProductDetailPromotionDTO(
//         // productDetailMapper.convertModelToProductDetailPromotionDTO(gift.getProductDetail()));

//         discountResponseDTO.setProductDetailId(gift.getProductDetail().getDetailId());

//         discountResponseDTO.setUnitPrice(gift.getProductDetail().getProductColor().getProduct().getUnitPrice());

//         discountResponseDTO.setProductImage(gift.getProductDetail().getProductColor().getProductImage());

//         discountResponseDTO.setProductSize(gift.getProductDetail().getSize());

//         discountResponseDTO.setProductColor(gift.getProductDetail().getProductColor().getColor());

//         discountResponseDTO.setProductQuantity(gift.getProductDetail().getQuantity());

//         return discountResponseDTO;
//     }

//     public Gift convertGiftRequestDTOToModel(GiftRequestDTO giftRequestDTO, Gift gift) {

//         gift.setGiftQuantity(giftRequestDTO.getGiftQuantity());

//         gift.setMaxGift(giftRequestDTO.getMaxGift());

//         return gift;
//     }
// }
