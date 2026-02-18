package com.example.clothingstore.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// import java.util.List;

import org.springframework.stereotype.Service;

import com.example.clothingstore.action.PromotionActionFactory;
import com.example.clothingstore.action.PromotionActionStrategy;
import com.example.clothingstore.condition.PromotionConditionFactory;
import com.example.clothingstore.condition.PromotionConditionStrategy;
// import com.example.clothingstore.dto.cart.CartRequestDTO;
import com.example.clothingstore.dto.cart.CartResponseDTO;
import com.example.clothingstore.dto.cartdetail.CartItemRequestDTO;
import com.example.clothingstore.dto.cartdetail.CartItemResponseDTO;
import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.customer.AccessDeniedException;
import com.example.clothingstore.exception.customer.ConflictException;
import com.example.clothingstore.exception.customer.NotFoundException;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    // @Autowired
    // private CartRepository cartRepository;

    // @Autowired
    // private CartDetailRepository cartDetailRepository;

    // @Autowired
    // private ProductDetailRepository productDetailRepository;

    // @Autowired
    // private CartMapper cartMapper;

    // @Autowired
    // private CartDetailMapper cartDetailMapper;

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

        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
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

        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Invalue Cart By Customer"));

        CartItem cartItem = cartDetailRepository.findByCartItemIdAndCart_CartId(cartDetailId, cart.getCartId())
                .orElseThrow(() -> new NotFoundException("Invalue"));

        CartItemResponseDTO cartItemResponseDTO = null;
        if (quantity != null) {
            if (quantity <= 0) {
                // cartItemResponseDTO =
                // cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);
                cartDetailRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(quantity);
            }
        }
        // if (isSelected != null) {
        // cartDetail.setIsSelected(isSelected);
        // // cartItemResponseDTO =
        // // cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);

        // }
        cartItemResponseDTO = cartDetailMapper.convertModelToCartItemResponseDTO(cartItem);

        return cartItemResponseDTO;
    }

    @Transactional
    public CartItemResponseDTO deleteCartItem(Integer customerId, Integer cartDetailId) {

        CartItem cartDetail = cartDetailRepository
                .findById(cartDetailId)
                .orElseThrow(() -> new NotFoundException("Invalue CartDetail Code"));

        if (!cartDetail.getCart().getCustomer().getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("You cannot delete items from another user's cart");
        }

        CartItemResponseDTO cartItemResponseDTO = cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);

        cartDetailRepository.delete(cartDetail);

        return cartItemResponseDTO;

    }

    @Transactional
    public OrderPreviewDTO previewOrder(Integer customerId, List<Integer> cartItemIds, List<Integer> promotionIds) {

        // Kiểm tra xem cartItemIds có thuộc về customerId hay không
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Invalue Cart By Customer"));

        List<CartItem> cartItems = cartDetailRepository.findAllById(cartItemIds);

        if (cartItems.size() != cartItemIds.size() || cartItems.isEmpty()) {
            throw new NotFoundException("Some cart items not found");
        }

        for (CartItem cartItem : cartItems) {
            if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
                throw new AccessDeniedException("You cannot preview items from another user's cart");
            }
        }

        // Tạo OrderPreviewDTO ban đầu

        Set<OrderDetailPreviewDTO> orderDetails = cartItems.stream()
                .map(cartItem -> {

                    final Double discountAmount = 0.0;

                    final Double price = cartItem.getProductDetail().getProductColor().getProduct().getUnitPrice();

                    final Double finalPrice = price - discountAmount; // Tính giá cuối cùng sau khi áp dụng giảm giá

                    final Boolean isFree = false;

                    OrderDetailPreviewDTO orderDetailPreviewDTO = OrderDetailPreviewDTO.builder()
                            .productDetailId(cartItem.getProductDetail().getDetailId())
                            .productName(cartItem.getProductDetail().getProductColor().getProduct().getProductName())
                            .productImage(cartItem.getProductDetail().getProductColor().getProductImage())
                            .color(cartItem.getProductDetail().getProductColor().getColor())
                            .size(cartItem.getProductDetail().getSize())
                            .quantity(cartItem.getQuantity())
                            .price(price)
                            .discountAmount(discountAmount)
                            .finalPrice(finalPrice)
                            .isFree(isFree)
                            .build();
                    return orderDetailPreviewDTO;
                })
                .collect(Collectors.toSet());

        Double totalAmount = orderDetails.stream()
                .mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity())
                .sum();

        Double discountAmount = 0.0; // Tính tổng số tiền giảm giá từ các chiến lược khuyến mãi áp dụng

        Double shippingFee = 30000.0; // Tính phí vận chuyển

        Double discountShippingFee = 0.0; // Tính số tiền giảm giá cho phí vận chuyển từ các chiến lược khuyến mãi áp
                                          // dụng

        Double finalAmount = totalAmount - discountAmount + shippingFee - discountShippingFee;

        OrderPreviewDTO orderPreviewDTO = OrderPreviewDTO.builder()
                .orderDetails(orderDetails)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .discountShippingFee(discountShippingFee)
                .finalAmount(finalAmount)
                .build();

        // Áp dụng khuyến mãi tự động không cần nhập mã khuyến mãi
        List<Promotion> automaticPromotions = promotionRepository.findByPromotionTypeAndStartDateBeforeAndEndDateAfter(
                PromotionTypeEnum.AUTOMATIC, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        for (Promotion promotion : automaticPromotions) {
            if (promotion.getIsActive() == null ||
                    !promotion.getIsActive() ||
                    promotion.getStartDate() == null ||
                    promotion.getEndDate() == null ||
                    promotion.getStartDate().isAfter(java.time.LocalDateTime.now()) ||
                    promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
                throw new NotFoundException("Promotion is not active");
            }

            List<PromotionCondition> conditions = promotion.getPromotionConditions();

            boolean isApplicable = true;
            for (PromotionCondition condition : conditions) {

                PromotionConditionTypeEnum conditionType = condition.getConditionType();

                PromotionConditionStrategy conditionFactory = promotionConditionFactory
                        .getPromotionConditionStrategy(conditionType);

                Map<String, Object> conditionValue = condition.getValue();

                if (!conditionFactory.isSatisfied(orderPreviewDTO, conditionValue)) {
                    isApplicable = false;
                    break;
                }

            }

            if (isApplicable) {

                List<PromotionAction> actions = promotion.getPromotionActions();

                for (PromotionAction action : actions) {

                    PromotionActionTypeEnum actionType = action.getActionType();

                    Map<String, Object> actionValue = action.getValue();

                    PromotionActionStrategy actionFactory = promotionActionFactory
                            .getPromotionActionStrategy(actionType);

                    actionFactory.execute(orderPreviewDTO, actionValue);

                }

            }

        }

        // Áp dụng khuyến mãi do nhập mã khuyến mãi
        if (promotionIds == null || promotionIds.isEmpty()) {
            return orderPreviewDTO;
        }

        List<Promotion> promotions = promotionRepository.findAllById(promotionIds);

        if (promotions.size() != promotionIds.size()) {
            throw new NotFoundException("Some promotions not found");
        }

        List<VoucherWallet> voucherWallets = voucherWalletRepository
                .findByPromotion_PromotionIdInAndCustomer_CustomerId(promotionIds,
                        customerId);

        if (voucherWallets.size() != promotionIds.size()) {
            throw new NotFoundException("Some promotions not found in customer's voucher wallet");
        }

        for (Promotion promotion : promotions) {

            if (promotion.getIsActive() == null ||
                    !promotion.getIsActive() ||
                    promotion.getStartDate() == null ||
                    promotion.getEndDate() == null ||
                    promotion.getStartDate().isAfter(java.time.LocalDateTime.now()) ||
                    promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
                throw new NotFoundException("Promotion is not active");
            }

            List<PromotionCondition> conditions = promotion.getPromotionConditions();

            boolean isApplicable = true;
            for (PromotionCondition condition : conditions) {

                PromotionConditionTypeEnum conditionType = condition.getConditionType();

                PromotionConditionStrategy conditionFactory = promotionConditionFactory
                        .getPromotionConditionStrategy(conditionType);

                Map<String, Object> conditionValue = condition.getValue();

                if (!conditionFactory.isSatisfied(orderPreviewDTO, conditionValue)) {
                    isApplicable = false;
                    break;
                }

            }

            if (isApplicable) {

                List<PromotionAction> actions = promotion.getPromotionActions();

                for (PromotionAction action : actions) {

                    PromotionActionTypeEnum actionType = action.getActionType();

                    Map<String, Object> actionValue = action.getValue();

                    PromotionActionStrategy actionFactory = promotionActionFactory
                            .getPromotionActionStrategy(actionType);

                    actionFactory.execute(orderPreviewDTO, actionValue);

                }

            }

        }

        return orderPreviewDTO;
    }
}
