package com.example.clothingstore.mapper;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.product.ProductDetailPromotionDTO;
import com.example.clothingstore.dto.product.ProductDetailResponseDTO;
import com.example.clothingstore.model.ProductDetail;

@Component
public class ProductDetailMapper {

    public ProductDetailResponseDTO convertModelToProductDetailResponseDTO(ProductDetail productDetail) {

        ProductDetailResponseDTO productDetailResponseDTO = new ProductDetailResponseDTO();

        productDetailResponseDTO.setDetailId(productDetail.getDetailId());

        productDetailResponseDTO.setSize(productDetail.getSize());

        productDetailResponseDTO.setQuantity(productDetail.getQuantity());

        return productDetailResponseDTO;
    }

    public ProductDetailPromotionDTO convertModeDetailPromotionDTO(ProductDetail productDetail) {

        ProductDetailPromotionDTO productDetailPromotionDTO = new ProductDetailPromotionDTO();

        productDetailPromotionDTO.setDetailId(productDetail.getDetailId());

        productDetailPromotionDTO.setSize(productDetail.getSize());

        productDetailPromotionDTO.setStock(productDetail.getQuantity());

        return productDetailPromotionDTO;
    }

    // public ProductDetailPromotionDTO
    // convertModelToProductDetailPromotionDTO(ProductDetail productDetail) {

    // ProductDetailPromotionDTO productDetailPromotionDTO = new
    // ProductDetailPromotionDTO();

    // productDetailPromotionDTO.setDetailId(productDetail.getDetailId());

    // productDetailPromotionDTO.setProductName(productDetail.getProductColor().getProduct().getProductName());

    // productDetailPromotionDTO.setColor(productDetail.getProductColor().getColor());

    // productDetailPromotionDTO.setSize(productDetail.getSize());

    // productDetailPromotionDTO.setImage(productDetail.getProductColor().getProductImage());

    // productDetailPromotionDTO.setUnitPrice(productDetail.getProductColor().getProduct().getUnitPrice());

    // productDetailPromotionDTO.setStock(productDetail.getQuantity());

    // return productDetailPromotionDTO;
    // }
}

