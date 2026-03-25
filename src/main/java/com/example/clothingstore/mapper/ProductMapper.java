package com.example.clothingstore.mapper;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.product.ProductPromotionDTO;
import com.example.clothingstore.dto.product.ProductSummaryDTO;
import com.example.clothingstore.model.Product;

@Component
public class ProductMapper {

    public ProductSummaryDTO convertModelToProductSummaryDTO(Product product) {
        ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO();

        productSummaryDTO.setProductId(product.getProductId());

        productSummaryDTO.setProductName(product.getProductName());

        productSummaryDTO.setUnitPrice(product.getUnitPrice());

        productSummaryDTO.setDescription(product.getDescription());

        productSummaryDTO.setProductImage(product.getProductImage());

        return productSummaryDTO;
    }

    // public ProductPromotionDTO convertModelToProductPromotionDTO(Product product)
    // {

    // }
}
