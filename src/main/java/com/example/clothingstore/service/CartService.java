package com.example.clothingstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// import java.util.List;

import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.cart.CartItemRequestDTO;
import com.example.clothingstore.dto.cart.CartItemResponseDTO;
// import com.example.clothingstore.dto.cart.CartRequestDTO;
import com.example.clothingstore.dto.cart.CartResponseDTO;
import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;
import com.example.clothingstore.dto.product.CreatePreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.business.AccessDeniedException;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.CartDetailMapper;
import com.example.clothingstore.mapper.CartMapper;
import com.example.clothingstore.model.Cart;
import com.example.clothingstore.model.CartItem;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.model.PromotionAction;
import com.example.clothingstore.model.PromotionCondition;
import com.example.clothingstore.model.VoucherWallet;
import com.example.clothingstore.repository.CartDetailRepository;
import com.example.clothingstore.repository.CartRepository;
import com.example.clothingstore.repository.ProductDetailRepository;
import com.example.clothingstore.repository.PromotionRepository;
import com.example.clothingstore.repository.VoucherWalletRepository;
import com.example.clothingstore.strategy.action.PromotionActionFactory;
import com.example.clothingstore.strategy.action.PromotionActionStrategy;
import com.example.clothingstore.strategy.condition.PromotionConditionFactory;
import com.example.clothingstore.strategy.condition.PromotionConditionStrategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {


    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CartMapper cartMapper;
    private final CartDetailMapper cartDetailMapper;

    private final PromotionRepository promotionRepository;

    private final PromotionConditionFactory promotionConditionFactory;
    private final PromotionActionFactory promotionActionFactory;

    private final VoucherWalletRepository voucherWalletRepository;

    @Transactional
    public CartResponseDTO getCartByCustomer(Integer customerId) {
        Cart cart = cartRepository.findByCustomerIdWithALLFetch(customerId)
                .orElseThrow(() -> new NotFoundException("Invalue Cart By Customer"));

        return cartMapper.convertModelTOCartResponseDTO(cart);
    }

    @Transactional
    public CartItemResponseDTO addCartItemByCart(Integer customerId, CartItemRequestDTO cartItemRequestDTO) {

        Cart cart = cartRepository.findByCustomer_UserId(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid Cart By Customer"));

        ProductDetail productDetail = productDetailRepository.findById(cartItemRequestDTO.getProductDetailId())
                .orElseThrow(() -> new NotFoundException("Invalid Product Detail Code"));

        Optional<CartItem> optionalCartItem = cartDetailRepository
                .findByCart_CartIdAndProductDetail_DetailId(cart.getCartId(), productDetail.getDetailId());

        CartItem cartItem = optionalCartItem.orElse(null);

        // Nếu cartItem đã tồn tại, cộng dồn số lượng
        Integer quantity = (cartItem == null) ? cartItemRequestDTO.getQuantity()
                : cartItem.getQuantity() + cartItemRequestDTO.getQuantity();

        if (quantity > productDetail.getQuantity()) {
            throw new ConflictException("MAX QUANTITY");
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProductDetail(productDetail);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
        } else {
            cartItem.setQuantity(quantity);
        }

        cartDetailRepository.save(cartItem);

        return cartDetailMapper.convertModelToCartItemResponseDTO(cartItem);
    }

    @Transactional
    public CartItemResponseDTO updateCartItem(Integer customerId, Integer cartDetailId, Integer quantity) {

        Cart cart = cartRepository.findByCustomer_UserId(customerId)
                .orElseThrow(() -> new NotFoundException("Invalue Cart By Customer"));

        CartItem cartItem = cartDetailRepository.findByCartItemIdAndCart_CartId(cartDetailId, cart.getCartId())
                .orElseThrow(() -> new NotFoundException("Invalue"));

        CartItemResponseDTO cartItemResponseDTO = null;
        if (quantity != null) {
            if (quantity <= 0) {
                cartDetailRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(quantity);
            }
        }
        cartItemResponseDTO = cartDetailMapper.convertModelToCartItemResponseDTO(cartItem);

        return cartItemResponseDTO;
    }

    @Transactional
    public CartItemResponseDTO deleteCartItem(Integer customerId, Integer cartDetailId) {

        CartItem cartDetail = cartDetailRepository
                .findById(cartDetailId)
                .orElseThrow(() -> new NotFoundException("Invalue CartDetail Code"));

        if (!cartDetail.getCart().getCustomer().getUserId().equals(customerId)) {
            throw new AccessDeniedException("You cannot delete items from another user's cart");
        }

        CartItemResponseDTO cartItemResponseDTO = cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);

        cartDetailRepository.delete(cartDetail);

        return cartItemResponseDTO;

    }

    // @Transactional
    // public OrderPreviewDTO previewOrder(Integer customerId, CreatePreviewDTO createPreviewDTO) {

    //     Map<Integer, Integer> productDetailIdToQuantity = createPreviewDTO.getDetails().stream()
    //             .collect(
    //                     Collectors.toMap(
    //                             CreatePreviewDTO.CreatePreviewDetailsDTO::getProductDetailIds,
    //                             CreatePreviewDTO.CreatePreviewDetailsDTO::getQuantity));

    //     List<ProductDetail> productDetails = productDetailRepository.findAllById(productDetailIdToQuantity.keySet());

    //     boolean isStockEnough = productDetails.stream()
    //             .allMatch(productDetail -> productDetail.getQuantity() >= productDetailIdToQuantity
    //                     .get(productDetail.getDetailId()));

    //     if (!isStockEnough) {
    //         throw new ConflictException("Some product details do not have enough stock");
    //     }

    //     Set<OrderDetailPreviewDTO> orderDetails = productDetails.stream()
    //             .map(productDetail -> {

    //                 final Double discountAmount = productDetail.getProductColor().getProduct()
    //                         .getDiscount() != null
    //                                 ? productDetail.getProductColor().getProduct().getUnitPrice()
    //                                         * (productDetail.getProductColor().getProduct().getDiscount() / 100)
    //                                 : 0.0;

    //                 final Double price = productDetail.getProductColor().getProduct().getUnitPrice();

    //                 final Double finalPrice = price - discountAmount;

    //                 final Boolean isFree = false;

    //                 OrderDetailPreviewDTO orderDetailPreviewDTO = OrderDetailPreviewDTO.builder()
    //                         .productDetailId(productDetail.getDetailId())
    //                         .productName(productDetail.getProductColor().getProduct().getProductName())
    //                         .productImage(productDetail.getProductColor().getProductImage())
    //                         .color(productDetail.getProductColor().getColor())
    //                         .size(productDetail.getSize())
    //                         .quantity(productDetailIdToQuantity.get(productDetail.getDetailId()))
    //                         .price(price)
    //                         .discountAmount(discountAmount)
    //                         .finalPrice(finalPrice)
    //                         .isFree(isFree)
    //                         .build();
    //                 return orderDetailPreviewDTO;
    //             }).collect(Collectors.toSet());

    //     Double totalAmount = orderDetails.stream()
    //             .mapToDouble(orderDetail -> orderDetail.getFinalPrice() * orderDetail.getQuantity())
    //             .sum();

    //     Double discountAmount = 0.0; // Tính tổng số tiền giảm giá từ các chiến lược khuyến mãi áp dụng

    //     Double shippingFee = 30000.0; // Tính phí vận chuyển

    //     Double discountShippingFee = 0.0; // Tính số tiền giảm giá cho phí vận chuyển từ các chiến lược khuyến mãi áp
    //                                       // dụng

    //     Double finalAmount = totalAmount - discountAmount + shippingFee - discountShippingFee;

    //     OrderPreviewDTO orderPreviewDTO = OrderPreviewDTO.builder()
    //             .orderDetails(orderDetails)
    //             .totalAmount(totalAmount)
    //             .discountAmount(discountAmount)
    //             .shippingFee(shippingFee)
    //             .discountShippingFee(discountShippingFee)
    //             .finalAmount(finalAmount)
    //             .appliedPromotions(new ArrayList<>())
    //             .build();

    //     // Áp dụng khuyến mãi tự động không cần nhập mã khuyến mãi
    //     List<Promotion> automaticPromotions = promotionRepository.findByPromotionTypeAndStartDateBeforeAndEndDateAfter(
    //             PromotionTypeEnum.AUTOMATIC, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

    //     for (Promotion promotion : automaticPromotions) {
    //         if (promotion.getIsActive() == null ||
    //                 !promotion.getIsActive() ||
    //                 promotion.getStartDate() == null ||
    //                 promotion.getEndDate() == null ||
    //                 promotion.getStartDate().isAfter(java.time.LocalDateTime.now()) ||
    //                 promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
    //             throw new NotFoundException("Promotion is not active");
    //         }

    //         boolean checkCondtion = promotion.getPromotionConditions().stream()
    //                 .allMatch((promotionCondition) -> promotionCondition
    //                         .getConditionType() == PromotionConditionTypeEnum.PRODUCT_SPECIFIC);

    //         if (checkCondtion) {
    //             continue; // Nếu tất cả điều kiện đều là PRODUCT_SPECIFIC thì bỏ qua vì không thể áp dụng
    //                       // nếu không nhập mã khuyến mãi
    //         }

    //         List<PromotionCondition> conditions = promotion.getPromotionConditions();

    //         boolean isApplicable = true;
    //         for (PromotionCondition condition : conditions) {

    //             PromotionConditionTypeEnum conditionType = condition.getConditionType();

    //             PromotionConditionStrategy conditionFactory = promotionConditionFactory
    //                     .getPromotionConditionStrategy(conditionType);

    //             Map<String, Object> conditionValue = condition.getValue();

    //             if (!conditionFactory.isSatisfied(orderPreviewDTO, conditionValue)) {
    //                 isApplicable = false;
    //                 break;
    //             }

    //         }

    //         if (isApplicable) {

    //             List<PromotionAction> actions = promotion.getPromotionActions();

    //             for (PromotionAction action : actions) {

    //                 PromotionActionTypeEnum actionType = action.getActionType();

    //                 // Map<String, Object> actionValue = action.getValue();

    //                 PromotionActionStrategy actionFactory = promotionActionFactory
    //                         .getPromotionActionStrategy(actionType);

    //                 actionFactory.execute(orderPreviewDTO, promotion, promotion.getPromotionActions().indexOf(action));

    //             }
    //             orderPreviewDTO.getAppliedPromotions().add(promotion.getPromotionId());

    //         }

    //     }

    //     List<Integer> promotionIds = createPreviewDTO.getPromotionIds();

    //     // Áp dụng khuyến mãi do nhập mã khuyến mãi
    //     if (promotionIds == null || promotionIds.isEmpty()) {
    //         return orderPreviewDTO;
    //     }

    //     List<Promotion> promotions = promotionRepository.findAllById(promotionIds);

    //     if (promotions.size() != promotionIds.size()) {
    //         throw new NotFoundException("Some promotions not found");
    //     }

    //     List<VoucherWallet> voucherWallets = voucherWalletRepository
    //             .findByPromotion_PromotionIdInAndCustomer_UserId(promotionIds,
    //                     customerId);

    //     if (voucherWallets.size() != promotionIds.size()) {
    //         throw new NotFoundException("Some promotions not found in customer's voucher wallet");
    //     }

    //     if (!hasAllStackable(promotions)) {
    //         throw new ConflictException("Some promotions cannot be stacked together");
    //     }

    //     for (Promotion promotion : promotions) {

    //         if (promotion.getIsActive() == null ||
    //                 !promotion.getIsActive() ||
    //                 promotion.getStartDate() == null ||
    //                 promotion.getEndDate() == null ||
    //                 promotion.getStartDate().isAfter(java.time.LocalDateTime.now()) ||
    //                 promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
    //             throw new NotFoundException("Promotion is not active");
    //         }

    //         List<PromotionCondition> conditions = promotion.getPromotionConditions();

    //         boolean isApplicable = true;
    //         for (PromotionCondition condition : conditions) {

    //             PromotionConditionTypeEnum conditionType = condition.getConditionType();

    //             PromotionConditionStrategy conditionFactory = promotionConditionFactory
    //                     .getPromotionConditionStrategy(conditionType);

    //             Map<String, Object> conditionValue = condition.getValue();

    //             if (!conditionFactory.isSatisfied(orderPreviewDTO, conditionValue)) {
    //                 isApplicable = false;
    //                 break;
    //             }

    //         }

    //         if (isApplicable) {

    //             List<PromotionAction> actions = promotion.getPromotionActions();

    //             for (PromotionAction action : actions) {

    //                 PromotionActionTypeEnum actionType = action.getActionType();

    //                 // Map<String, Object> actionValue = action.getValue();

    //                 PromotionActionStrategy actionFactory = promotionActionFactory
    //                         .getPromotionActionStrategy(actionType);

    //                 actionFactory.execute(orderPreviewDTO, promotion, promotion.getPromotionActions().indexOf(action));

    //             }
    //             orderPreviewDTO.getAppliedPromotions().add(promotion.getPromotionId());

    //         }

    //     }

    //     Set<Integer> promotionProductIds = productDetails.stream()
    //             .map((productDetail) -> productDetail.getProductColor().getProduct().getPromotion().getPromotionId())
    //             .collect(Collectors.toSet());

    //     orderPreviewDTO.getAppliedPromotions().addAll(promotionProductIds);

    //     return orderPreviewDTO;
    // }

    // private boolean hasAllStackable(List<Promotion> promotions) {
    //     if (promotions.size() <= 1) {
    //         return true; // Nếu chỉ có một khuyến mãi hoặc không có khuyến mãi nào, thì mặc định là có
    //                      // thể xếp chồng
    //     }
    //     return promotions.stream().allMatch(
    //             (promotion) -> promotion.getStackable() == true);
    // }
}
