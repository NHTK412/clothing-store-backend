// package com.example.clothingstore.mapper;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.example.clothingstore.dto.discount.DiscountResponseDTO;
// import com.example.clothingstore.dto.gift.GiftResponseDTO;
// import com.example.clothingstore.dto.promotion.PromotionRequestDTO;
// import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
// import com.example.clothingstore.dto.promotion.PromotionSummaryDTO;
// import com.example.clothingstore.dto.promotiongroup.PromotionGroupResponseDTO;
// import com.example.clothingstore.enums.PromotionTypeEnum;
// import com.example.clothingstore.model.Promotion;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor

// public class PromotionMapper {

//     // @Autowired
//     // private PromotionGroupMapper promotionGroupMapper;

//     // @Autowired
//     // private DiscountMapper discountMapper;

//     // @Autowired
//     // private GiftMapper giftMapper;

//     private final PromotionGroupMapper promotionGroupMapper;
//     private final DiscountMapper discountMapper;
//     private final GiftMapper giftMapper;

//     public PromotionResponseDTO convertModelToPromotionResponseDTO(Promotion promotion) {
//         PromotionResponseDTO promotionResponseDTO = new PromotionResponseDTO();

//         promotionResponseDTO.setPromotionId(promotion.getPromotionId());

//         promotionResponseDTO.setPromotionName(promotion.getPromotionName());

//         promotionResponseDTO.setPromotionType(promotion.getPromotionType());

//         promotionResponseDTO.setDescription(promotion.getDescription());

//         promotionResponseDTO.setStartDate(promotion.getStartDate());

//         promotionResponseDTO.setEndDate(promotion.getEndDate());

//         List<PromotionGroupResponseDTO> promotionGroupResponseDTOs = promotion.getPromotionGroups().stream()
//                 .map((promotionGroup) -> promotionGroupMapper.convertModelPromotionGroupResponseDTO(promotionGroup))
//                 .toList();

//         promotionResponseDTO.setPromotionGroupResponseDTOs(promotionGroupResponseDTOs);

//         if (promotion.getPromotionType() == PromotionTypeEnum.GIFT) {
//             List<GiftResponseDTO> giftResponseDTOs = promotion.getGits().stream()
//                     .map((gift) -> giftMapper.convertModelToDiscountResponseDTO(gift)).toList();

//             promotionResponseDTO.setGiftResponseDTOs(giftResponseDTOs);

//         } else {
//             promotionResponseDTO
//                     .setDiscountResponseDTO(discountMapper.convertModelToDiscountResponseDTO(promotion.getDiscount()));
//         }
//         return promotionResponseDTO;
//     }

//     public Promotion convertPromotionRequestDTOToModel(PromotionRequestDTO promotionRequestDTO, Promotion promotion) {
//         promotion.setPromotionName(promotionRequestDTO.getPromotionName());

//         promotion.setPromotionType(promotionRequestDTO.getPromotionType());

//         promotion.setDescription(promotionRequestDTO.getDescription());

//         promotion.setStartDate(promotionRequestDTO.getStartDate());

//         promotion.setEndDate(promotionRequestDTO.getEndDate());

//         return promotion;
//     }

//     public PromotionSummaryDTO convertModelToPromotionSummaryDTO(Promotion promotion) {
//         PromotionSummaryDTO promotionSummaryDTO = new PromotionSummaryDTO();

//         promotionSummaryDTO.setPromotionId(promotion.getPromotionId());

//         promotionSummaryDTO.setPromotionName(promotion.getPromotionName());

//         promotionSummaryDTO.setPromotionType(promotion.getPromotionType());

//         promotionSummaryDTO.setDescription(promotion.getDescription());

//         promotionSummaryDTO.setStartDate(promotion.getStartDate());

//         promotionSummaryDTO.setEndDate(promotion.getEndDate());

//         if (promotion.getPromotionType() == PromotionTypeEnum.DISCOUNT_AMOUNT) {

//             promotionSummaryDTO.setDiscountAmount(promotion.getDiscount().getDiscountAmount());


//         } else if (promotion.getPromotionType() == PromotionTypeEnum.DISCOUNT_PERCENTAGE) {
//             promotionSummaryDTO.setDiscountPercentage(promotion.getDiscount().getDiscountPercentage());
//         }

//         return promotionSummaryDTO;
//     }
// }
