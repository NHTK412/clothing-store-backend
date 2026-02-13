package com.example.clothingstore.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.product.ProductPromotionDTO;
import com.example.clothingstore.dto.productcolor.ProductColorPromotionDTO;
import com.example.clothingstore.dto.productdetail.ProductDetailPromotionDTO;
import com.example.clothingstore.dto.promotiongroup.PromotionGroupRequestDTO;
import com.example.clothingstore.dto.promotiongroup.PromotionGroupResponseDTO;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.ProductColor;
import com.example.clothingstore.model.PromotionGroup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PromotionGroupMapper {

        // @Autowired
        // private ProductDetailMapper productDetailMapper;

        private final ProductDetailMapper productDetailMapper;

        // @Autowired
        // private ProductMapper productMapper;

        public PromotionGroupResponseDTO convertModelPromotionGroupResponseDTO(PromotionGroup promotionGroup) {

                PromotionGroupResponseDTO promotionGroupResponseDTO = new PromotionGroupResponseDTO();

                promotionGroupResponseDTO.setGroupId(promotionGroup.getGroupId());

                promotionGroupResponseDTO.setGroupName(promotionGroup.getGroupName());

                promotionGroupResponseDTO.setDescription(promotionGroup.getDescription());

                promotionGroupResponseDTO.setMinPurchaseQuantity(promotionGroup.getMinPurchaseQuantity());

                List<ProductPromotionDTO> productPromotionDTOs = promotionGroup.getProductDetails()
                                .stream()
                                .collect(Collectors.groupingBy((productDetail) -> productDetail.getProductColor()
                                                .getProduct().getProductId()))
                                .entrySet()
                                .stream()
                                .map((productEntry) -> {

                                        Product product = productEntry.getValue().get(0).getProductColor().getProduct();

                                        ProductPromotionDTO productPromotionDTO = new ProductPromotionDTO();

                                        productPromotionDTO.setProductId(product.getProductId());

                                        productPromotionDTO.setProductName(product.getProductName());

                                        productPromotionDTO.setProductPrice(product.getUnitPrice());

                                        List<ProductColorPromotionDTO> productColorPromotionDTOs = productEntry
                                                        .getValue()
                                                        .stream()
                                                        .collect(Collectors.groupingBy((productDetail) -> productDetail
                                                                        .getProductColor()
                                                                        .getColorId()))
                                                        .entrySet()
                                                        .stream()
                                                        .map((productColorEntry) -> {

                                                                ProductColor productColor = productColorEntry
                                                                                .getValue()
                                                                                .get(0)
                                                                                .getProductColor();

                                                                ProductColorPromotionDTO productColorPromotionDTO = new ProductColorPromotionDTO();

                                                                productColorPromotionDTO
                                                                                .setColorId(productColor.getColorId());

                                                                productColorPromotionDTO
                                                                                .setColor(productColor.getColor());

                                                                productColorPromotionDTO
                                                                                .setColorImage(productColor.getColor());

                                                                List<ProductDetailPromotionDTO> productDetailPromotionDTOs = productColorEntry
                                                                                .getValue()
                                                                                .stream()
                                                                                .map((productDetail) -> {
                                                                                        return productDetailMapper
                                                                                                        .convertModeDetailPromotionDTO(
                                                                                                                        productDetail);
                                                                                })
                                                                                .toList();

                                                                productColorPromotionDTO.setProductDetails(
                                                                                productDetailPromotionDTOs);

                                                                return productColorPromotionDTO;

                                                                // productColorPromotionDTO.setProductDetails(null);

                                                        })
                                                        .toList();

                                        productPromotionDTO.setProductcolors(productColorPromotionDTOs);

                                        return productPromotionDTO;
                                })
                                .toList();

                promotionGroupResponseDTO.setProductPromotionDTOs(productPromotionDTOs);

                return promotionGroupResponseDTO;
        }

        public PromotionGroup convertProductGroupRequestDTOTOModel(PromotionGroupRequestDTO promotionGroupRequestDTO,
                        PromotionGroup promotionGroup) {

                promotionGroup.setGroupName(promotionGroupRequestDTO.getGroupName());

                promotionGroup.setDescription(promotionGroupRequestDTO.getDescription());

                promotionGroup.setMinPurchaseQuantity(promotionGroupRequestDTO.getMinPurchaseQuantity());

                return promotionGroup;
        }

        // public PromotionGroup convertProductGroupRequestDTOToModel(ProductGro)
}
