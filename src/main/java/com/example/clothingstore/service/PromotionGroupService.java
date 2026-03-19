package com.example.clothingstore.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.promotion.PromotionGroupRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionGroupResponseDTO;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.PromotionGroupMapper;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.PromotionGroup;
import com.example.clothingstore.repository.ProductRepository;
import com.example.clothingstore.repository.PromotionGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionGroupService {

    final private PromotionGroupRepository promotionGroupRepository;

    final private PromotionGroupMapper promotionGroupMapper;

    final private ProductRepository productRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deletePromotionGroupsIfNotAssigned() {

        List<PromotionGroup> promotionGroupsWithoutPromotion = promotionGroupRepository.findByPromotionIsNull();

        promotionGroupRepository.deleteAll(promotionGroupsWithoutPromotion);

    }

    @Transactional
    public PromotionGroupResponseDTO createPromotionGroup(PromotionGroupRequestDTO promotionGroupRequestDTO) {

        // PromotionGroup promotionGroup = PromotionGroup.builder()
        // .groupName(promotionGroupRequestDTO.getGroupName())
        // .description(promotionGroupRequestDTO.getDescription())
        // .build();

        PromotionGroup promotionGroup = new PromotionGroup();

        promotionGroup.setGroupName(promotionGroupRequestDTO.getGroupName());
        promotionGroup.setDescription(promotionGroupRequestDTO.getDescription());

        List<Product> products = productRepository.findAllById(promotionGroupRequestDTO.getProductIds());

        if (products.size() != promotionGroupRequestDTO.getProductIds().size()) {
            throw new IllegalArgumentException("One or more product IDs are invalid.");
        }

        promotionGroup.setProducts(products);

        PromotionGroup savedPromotionGroup = promotionGroupRepository.save(promotionGroup);

        return promotionGroupMapper.toResponseDTO(savedPromotionGroup);
    }

    @Transactional
    public PromotionGroupResponseDTO getPromotionGroupById(Integer groupId) {

        PromotionGroup promotionGroup = promotionGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Promotion group not found with ID: " + groupId));

        return promotionGroupMapper.toResponseDTO(promotionGroup);

    }

}
