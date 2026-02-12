package com.example.clothingstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.cart.CartCheckPromotionDTO;
import com.example.clothingstore.dto.promotion.PromotionRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
import com.example.clothingstore.dto.promotion.PromotionSummaryDTO;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.mapper.DiscountMapper;
import com.example.clothingstore.mapper.GiftMapper;
import com.example.clothingstore.mapper.PromotionGroupMapper;
import com.example.clothingstore.mapper.PromotionMapper;
import com.example.clothingstore.model.Discount;
import com.example.clothingstore.model.Gift;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.model.PromotionGroup;
import com.example.clothingstore.repository.ProductDetailRepository;
import com.example.clothingstore.repository.PromotionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {

    // @Autowired
    // private PromotionRepository promotionRepository;

    // @Autowired
    // private ProductDetailRepository productDetailRepository;

    // @Autowired
    // private PromotionMapper promotionMapper;

    // @Autowired
    // private PromotionGroupMapper promotionGroupMapper;

    // @Autowired
    // private GiftMapper giftMapper;

    // @Autowired
    // private DiscountMapper discountMapper;

    private final PromotionRepository promotionRepository;
    private final ProductDetailRepository productDetailRepository;
    private final PromotionMapper promotionMapper;
    private final PromotionGroupMapper promotionGroupMapper;
    private final GiftMapper giftMapper;
    private final DiscountMapper discountMapper;

    @Transactional
    public PromotionResponseDTO getPromotionById(Integer promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("Invalue Promotion Code"));

        return promotionMapper.convertModelToPromotionResponseDTO(promotion);
    }

    @Transactional
    public PromotionResponseDTO createPromotion(PromotionRequestDTO dto) {

        // Promotion promotion = ;

        // Convert DTO → Model
        Promotion promotion = promotionMapper.convertPromotionRequestDTOToModel(dto, new Promotion());

        List<PromotionGroup> groups = dto.getPromotionGroupRequestDTOs()
                .stream()
                .map(groupDTO -> {

                    // PromotionGroup group = new PromotionGroup();
                    PromotionGroup group = promotionGroupMapper
                            .convertProductGroupRequestDTOTOModel(groupDTO, new PromotionGroup());

                    // Lấy danh sách ProductDetail
                    List<ProductDetail> productDetails = productDetailRepository
                            .findAllById(groupDTO.getProductDetailIds());

                    group.setProductDetails(productDetails);

                    // Set quan hệ 2 chiều
                    group.setPromotion(promotion);

                    return group;
                })
                .collect(Collectors.toList());

        promotion.setPromotionGroups(groups);

        if (dto.getPromotionType() == PromotionTypeEnum.GIFT) {

            List<Gift> gifts = dto.getGiftRequestDTOs()
                    .stream()
                    .map(giftDTO -> {
                        // Gift gift = new Gift();

                        Gift gift = giftMapper.convertGiftRequestDTOToModel(giftDTO, new Gift());

                        gift.setProductDetail(
                                productDetailRepository.findById(giftDTO.getProductDetailId())
                                        .orElseThrow(() -> new NotFoundException("Invalid Product Code")));

                        gift.setPromotion(promotion);

                        return gift;
                    })
                    .collect(Collectors.toList());

            promotion.setGits(gifts);

        } else {
            // Discount discount = new Discount();

            boolean isAmount = dto.getPromotionType() == PromotionTypeEnum.DISCOUNT_AMOUNT;

            Discount discount = discountMapper.convertDiscountRequestDTOTOModel(
                    dto.getDiscountRequestDTO(),
                    new Discount(),
                    isAmount);

            discount.setPromotion(promotion);

            promotion.setDiscount(discount);
        }

        promotionRepository.save(promotion);

        return promotionMapper.convertModelToPromotionResponseDTO(promotion);
    }

    @Transactional
    public List<PromotionSummaryDTO> getApplicableDiscountPromotion(CartCheckPromotionDTO cartCheckPromotionDTO) {

        List<Promotion> promotions = promotionRepository
                .findByPromotionTypeIn(
                        List.of(PromotionTypeEnum.DISCOUNT_AMOUNT, PromotionTypeEnum.DISCOUNT_PERCENTAGE));

        List<PromotionSummaryDTO> promotionSummaryDTOs = new ArrayList<>();

        List<Integer> productDetailIds = cartCheckPromotionDTO.getCartItems()
                .stream()
                .map(item -> item.getProductDetailId())
                .collect(Collectors.toList());

        Map<Integer, ProductDetail> productDetailsInCart = productDetailRepository.findAllById(productDetailIds)
                .stream()
                .collect(Collectors.toMap(
                        productDetail -> productDetail.getDetailId(),
                        productDetail -> productDetail));

        Map<ProductDetail, Integer> productDetailsInCartMap = cartCheckPromotionDTO
                .getCartItems()
                .stream()
                .collect(Collectors.toMap(cartItem -> productDetailsInCart.get(cartItem.getProductDetailId()),
                        cartItem -> cartItem.getQuantity()));

        // List<Integer> productDetailIds = cartCheckPromotionDTO.getCartItems()
        // .stream()
        // .map(item -> item.getProductDetailId())
        // .collect(Collectors.toList());

        // List<ProductDetail> productDetailsInCart = productDetailRepository
        // .findAllById(productDetailIds);

        // Map<ProductDetail, Integer> productDetailQuantityMap =
        // productDetailRepository
        // .findAllById(productDetailIds).stream()
        // .collect(Collectors.toMap(
        // productDetail -> productDetail
        // ,
        // ));

        for (Promotion promotion : promotions) {
            if (isProductInPromotionGroup(productDetailsInCartMap, promotion)) {

                PromotionSummaryDTO promotionSummaryDTO = promotionMapper.convertModelToPromotionSummaryDTO(promotion);

                promotionSummaryDTOs.add(promotionSummaryDTO);

            }
        }

        return promotionSummaryDTOs;
    }

    // Hàm này check xem ProductDetail có trong PromotionGroup của Promotion không
    // private Boolean isProductInPromotionGroup(List<ProductDetail>
    // productDetailsInCart, Promotion promotion) {
    private Boolean isProductInPromotionGroup(Map<ProductDetail, Integer> productDetailsInCartMap,
            Promotion promotion) {

        List<PromotionGroup> promotionGroups = promotion.getPromotionGroups();

        Set<ProductDetail> productDetails = productDetailsInCartMap.keySet();

        for (PromotionGroup group : promotionGroups) {

            Integer count = 0;

            for (ProductDetail productDetailInCart : productDetails) {
                if (group.getProductDetails().contains(productDetailInCart)) {
                    count = count + productDetailsInCartMap.get(productDetailInCart);
                }
            }
            if (count < group.getMinPurchaseQuantity()) {
                return false;
            }

        }
        return true;
    }

    @Transactional
    public List<PromotionSummaryDTO> getApplicablePromotion(CartCheckPromotionDTO cartCheckPromotionDTO,
            List<PromotionTypeEnum> promotionTypes) {

        List<Promotion> promotions = promotionRepository
                .findByPromotionTypeIn(promotionTypes);

        List<PromotionSummaryDTO> promotionSummaryDTOs = new ArrayList<>();

        List<Integer> productDetailIds = cartCheckPromotionDTO.getCartItems()
                .stream()
                .map(item -> item.getProductDetailId())
                .collect(Collectors.toList());

        Map<Integer, ProductDetail> productDetailsInCart = productDetailRepository.findAllById(productDetailIds)
                .stream()
                .collect(Collectors.toMap(
                        productDetail -> productDetail.getDetailId(),
                        productDetail -> productDetail));

        Map<ProductDetail, Integer> productDetailsInCartMap = cartCheckPromotionDTO
                .getCartItems()
                .stream()
                .collect(Collectors.toMap(cartItem -> productDetailsInCart.get(cartItem.getProductDetailId()),
                        cartItem -> cartItem.getQuantity()));

        // List<Integer> productDetailIds = cartCheckPromotionDTO.getCartItems()
        // .stream()
        // .map(item -> item.getProductDetailId())
        // .collect(Collectors.toList());

        // List<ProductDetail> productDetailsInCart = productDetailRepository
        // .findAllById(productDetailIds);

        // Map<ProductDetail, Integer> productDetailQuantityMap =
        // productDetailRepository
        // .findAllById(productDetailIds).stream()
        // .collect(Collectors.toMap(
        // productDetail -> productDetail
        // ,
        // ));

        for (Promotion promotion : promotions) {
            if (isProductInPromotionGroup(productDetailsInCartMap, promotion)) {

                PromotionSummaryDTO promotionSummaryDTO = promotionMapper.convertModelToPromotionSummaryDTO(promotion);

                promotionSummaryDTOs.add(promotionSummaryDTO);

            }
        }

        return promotionSummaryDTOs;
    }

    public List<PromotionSummaryDTO> getAllPromotions(PageRequest pageable) {

        Page<Promotion> promotions = promotionRepository.findAll(pageable);

        List<PromotionSummaryDTO> promotionSummaryDTOs = promotions
                .stream()
                .map(promotion -> promotionMapper.convertModelToPromotionSummaryDTO(promotion))
                .collect(Collectors.toList());

        return promotionSummaryDTOs;
    }

    @Transactional
    public PromotionResponseDTO deletePromotion(Integer promotionId) {

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("Invalue Promotion Code"));

        PromotionResponseDTO promotionResponseDTO = promotionMapper.convertModelToPromotionResponseDTO(promotion);

        promotionRepository.delete(promotion);

        return promotionResponseDTO;
    }
}
