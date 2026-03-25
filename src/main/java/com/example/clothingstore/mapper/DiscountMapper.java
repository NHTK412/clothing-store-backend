// package com.example.clothingstore.mapper;

// import org.apache.logging.log4j.message.ReusableMessage;
// import org.springframework.stereotype.Component;

// import com.example.clothingstore.dto.discount.DiscountRequestDTO;
// import com.example.clothingstore.dto.discount.DiscountResponseDTO;
// import com.example.clothingstore.model.Discount;

// @Component
// public class DiscountMapper {

//     public DiscountResponseDTO convertModelToDiscountResponseDTO(Discount discount) {
//         DiscountResponseDTO discountResponseDTO = new DiscountResponseDTO();

//         discountResponseDTO.setDiscountId(discount.getDiscountId());

//         discountResponseDTO.setDiscountPercentage(discount.getDiscountPercentage());

//         discountResponseDTO.setDiscountAmount(discount.getDiscountAmount());

//         discountResponseDTO.setMaxDiscount(discount.getMaxDiscount());

//         return discountResponseDTO;
//     }

//     public Discount convertDiscountRequestDTOTOModel(DiscountRequestDTO discountRequestDTO, Discount discount,
//             Boolean type) {

//         if (type) {

//             discount.setDiscountAmount(discount.getDiscountAmount());

//         } else {

//             discount.setDiscountPercentage(discount.getDiscountPercentage());

//         }
//         discount.setMaxDiscount(discount.getMaxDiscount());

//         return discount;
//     }
// }
