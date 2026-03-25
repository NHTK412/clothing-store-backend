package com.example.clothingstore.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.order.OrderRequestDTO;
import com.example.clothingstore.dto.order.OrderResponseDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailRequestDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailResponseDTO;
import com.example.clothingstore.dto.product.CreatePreviewDTO;
import com.example.clothingstore.dto.product.CreatePreviewDTO.CreatePreviewDetailsDTO;
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.OrderTypeEnum;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.enums.RefundMethodEnum;
import com.example.clothingstore.enums.RefundRequestStatusEnum;
import com.example.clothingstore.enums.RoleEnum;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.OrderMapper;
import com.example.clothingstore.model.Cart;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.model.OrderDetail;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.model.PromotionAction;
import com.example.clothingstore.model.PromotionCondition;
import com.example.clothingstore.model.RefundItem;
import com.example.clothingstore.model.RefundRequest;
import com.example.clothingstore.model.User;
import com.example.clothingstore.model.VoucherWallet;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.repository.CartRepository;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.OrderRepository;
import com.example.clothingstore.repository.ProductDetailRepository;
import com.example.clothingstore.repository.PromotionRepository;
import com.example.clothingstore.repository.RefundRequestRepository;
import com.example.clothingstore.repository.UserRepository;
import com.example.clothingstore.repository.VoucherWalletRepository;
import com.example.clothingstore.strategy.action.PromotionActionFactory;
import com.example.clothingstore.strategy.action.PromotionActionStrategy;
import com.example.clothingstore.strategy.condition.PromotionConditionFactory;
import com.example.clothingstore.strategy.condition.PromotionConditionStrategy;
import com.example.clothingstore.validator.OrderValidator;
import com.example.clothingstore.validator.PromotionValidator;
import com.example.clothingstore.validator.UserValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final ProductDetailRepository productDetailRepository;
        private final PromotionRepository promotionRepository;
        private final CustomerRepository customerRepository;
        private final CartRepository cartRepository;
        private final VoucherWalletRepository voucherWalletRepository;
        private final UserRepository userRepository;
        private final RefundRequestRepository refundRequestRepository;

        private final PromotionConditionFactory promotionConditionFactory;
        private final PromotionActionFactory promotionActionFactory;

        private final UserValidator userValidator;
        private final PromotionValidator promotionValidator;
        private final OrderValidator orderValidator;

        private final OrderMapper orderMapper;

        private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OrderService.class);

        @Transactional
        public OrderResponseDTO getOrderById(Integer orderId, Integer userId) {
                User user = userValidator.validateAndGetUser(userId);
                Order order = (user.getRole() == RoleEnum.ROLE_CUSTOMER)
                                ? orderValidator.validateAndGetOrderForCustomer(orderId, userId)
                                : orderValidator.validateAndGetOrder(orderId);
                return orderMapper.toResponseDTO(order);
        }

        @Transactional
        public Page<OrderSummaryDTO> getAllOrdersByCustomer(Integer customerId, Pageable pageable) {
                Page<Order> orders = orderRepository.findAllByCustomerId(customerId, pageable);
                return orders.map(
                                order -> orderMapper.toSummaryDTO(order));
        }

        @Transactional
        public Page<OrderSummaryDTO> getAllOrders(Pageable pageable) {
                Page<Order> orders = orderRepository.findAll(pageable);
                return orders.map((order) -> orderMapper.toSummaryDTO(order));
        }

        @Transactional
        public OrderResponseDTO updateStatus(Integer orderId, OrderStatusEnum status) {
                if (status == OrderStatusEnum.CANCELED) {
                        throw new ConflictException("Cannot update order status to CANCELED from this endpoint");
                }
                Order order = orderValidator.validateAndGetOrder(orderId);
                orderValidator.isValidStatusTransition(order.getStatus(), status);
                switch (status) {
                        case DELIVERED:
                                order.setPaymentStatus(OrderPaymentStatusEnum.PAID);
                                order.setDeliveryDate(java.time.LocalDateTime.now());
                                break;
                        default:
                                break;
                }
                order.setStatus(status);
                orderRepository.save(order);
                return orderMapper.toResponseDTO(order);
        }

        private void updateCartAfterOrder(
                        Integer customerId,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                Cart cart = cartRepository.findByCustomer_UserId(customerId)
                                .orElseThrow(() -> new NotFoundException("Cart not found"));
                cart.getCartItems().removeIf(cartItem -> {
                        Integer productDetailId = cartItem.getProductDetail().getDetailId();
                        return orderDetailRequestMap.containsKey(productDetailId);
                });
                cartRepository.save(cart);
        }

        @Transactional
        public OrderResponseDTO cancelOrder(
                        Integer customerId,
                        Integer orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));
                if (!order.getCustomer().getUserId().equals(customerId)) {
                        throw new ConflictException("You can only cancel your own orders");
                }
                orderValidator.isValidStatusTransition(order.getStatus(), OrderStatusEnum.CANCELED);
                order.setStatus(OrderStatusEnum.CANCELED);
                refundProduct(order);
                refundVoucher(order);
                if (order.getPaymentStatus() == OrderPaymentStatusEnum.PAID) {
                        refundPayment(order);
                }
                orderRepository.save(order);
                return getOrderById(order.getOrderId(), customerId);
        }

        private void refundPayment(Order order) {
                RefundRequest refundRequest = new RefundRequest();
                refundRequest.setOrder(order);
                refundRequest.setReason("Refund for canceled order");
                refundRequest.setStatus(RefundRequestStatusEnum.APPROVED);
                refundRequest.setRefundMethod(RefundMethodEnum.ZALOPAY);
                refundRequest.setRefundShippingFee(BigDecimal.valueOf(
                                order.getShippingFee() - order.getDiscountShippingFee()));
                List<RefundItem> refundItems = order.getOrderDetails().stream()
                                .map(od -> {
                                        RefundItem refundItem = new RefundItem();
                                        refundItem.setProductDetail(od.getProductDetail());
                                        refundItem.setQuantity(od.getQuantity());
                                        refundItem.setRefundAmount(BigDecimal.valueOf(od.getFinalPrice()));
                                        refundItem.setRefundRequest(refundRequest);
                                        return refundItem;
                                })
                                .toList();
                refundRequest.setRefundItems(refundItems);
                order.getRefundRequests().add(refundRequest);

        }

        private void refundProduct(Order order) {
                for (OrderDetail od : order.getOrderDetails()) {
                        ProductDetail pd = od.getProductDetail();
                        pd.setQuantity(pd.getQuantity() + od.getQuantity());
                        productDetailRepository.save(pd);
                }

        }

        private void refundVoucher(
                        Order order) {
                List<Promotion> appliedPromotions = order.getPromotions();
                Customer customer = order.getCustomer();
                Set<Promotion> customerPromotions = appliedPromotions.stream()
                                .filter(promo -> promo.getPromotionType() != PromotionTypeEnum.AUTOMATIC)
                                .collect(Collectors.toSet());
                List<VoucherWallet> voucherWalletsToReturn = customerPromotions.stream()
                                .map(
                                                (promotion) -> {
                                                        VoucherWallet voucherWallet = new VoucherWallet();
                                                        voucherWallet.setCustomer(customer);
                                                        voucherWallet.setPromotion(promotion);
                                                        return voucherWallet;
                                                })
                                .toList();
                voucherWalletRepository.saveAll(voucherWalletsToReturn);
                orderRepository.save(order);
        }

        // Thiết kế lại logic tạo đơn xem trước và tạo đơn hàng dựa trên OrderPreviewDTO
        // để tránh lặp code và đảm bảo tính nhất quán giữa hai luồng này
        @Transactional
        public OrderPreviewDTO createPreviewOrder_v2(Integer customerId, CreatePreviewDTO createPreviewDTO) {

                // Lấy ra customer
                Customer customer = userValidator.validateAndGetCustomer(customerId);

                // Lấy ra danh sách các mã khuyến mãi mà khách hàng muốn áp dụng
                Set<Integer> voucherPromotionIds = new HashSet<>(createPreviewDTO.getPromotionIds());

                Set<Promotion> voucherPromotions = promotionValidator.validationPromotions(voucherPromotionIds);

                boolean hasStackable = voucherPromotions
                                .stream()
                                .anyMatch(promotion -> promotion.getStackable() == true);

                if (hasStackable == false && voucherPromotions.size() > 1) {
                        throw new ConflictException("Some promotions cannot be stacked together");
                }

                if (promotionValidator.validationActivePromotion(voucherPromotions) == false) {
                        throw new NotFoundException("Some promotions are not active");
                }

                // Lấy danh sách các sản phẩm
                Map<Integer, Integer> productDetailIdToQuantity = createPreviewDTO
                                .getDetails()
                                .stream()
                                .collect(Collectors.toMap(
                                                CreatePreviewDTO.CreatePreviewDetailsDTO::getProductDetailIds,
                                                CreatePreviewDTO.CreatePreviewDetailsDTO::getQuantity));

                List<ProductDetail> productDetails = productDetailRepository
                                .findAllById(productDetailIdToQuantity.keySet());

                if (productDetails.size() != productDetailIdToQuantity.keySet().size()) {
                        throw new NotFoundException("Some product details not found");
                }

                // Lấy các khuyến mãi tự động áp dụng
                Set<Promotion> autoPromotions = promotionRepository
                                .findByPromotionTypeAndIsActive(PromotionTypeEnum.AUTOMATIC, true).stream()
                                .collect(Collectors.toSet());

                // Lọc ra các khuyến mãi đã được áp dụng tự động thông qua quan hệ giữa
                // Product và Promotion để tránh áp dụng trùng lặp
                Set<Promotion> productPromotions = productDetails.stream()
                                .map((productDetail) -> productDetail.getProductColor().getProduct().getPromotion())
                                .filter((promotion) -> promotion != null)
                                .collect(Collectors.toSet());

                if (promotionValidator.validationActivePromotion(productPromotions) == false) {
                        throw new NotFoundException("Some promotions are not active");
                }

                // Gộp tất cả các khuyến mãi lại với nhau
                Set<Promotion> allPromotions = new HashSet<>();
                allPromotions.addAll(voucherPromotions);
                allPromotions.addAll(autoPromotions);
                allPromotions.addAll(productPromotions);

                // Tạo OrderPreviewDTO từ thông tin sản phẩm và khuyến mãi
                Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap = createPreviewDTO.getDetails().stream()
                                .collect(Collectors.toMap(
                                                (detail) -> detail.getProductDetailIds(),
                                                (detail) -> {
                                                        OrderDetailRequestDTO orderDetailRequestDTO = new OrderDetailRequestDTO();
                                                        orderDetailRequestDTO.setProductDetailId(
                                                                        detail.getProductDetailIds());
                                                        orderDetailRequestDTO.setQuantity(detail.getQuantity());
                                                        return orderDetailRequestDTO;
                                                }));

                Set<OrderDetailPreviewDTO> orderDetailPreviews = orderMapper.toSetDetailPreviewDTO(productDetails,
                                orderDetailRequestMap);

                OrderPreviewDTO orderPreviewDTO = orderMapper.toPreviewDTO(orderDetailPreviews);

                // Áp dụng tất cả các khuyến mãi lên OrderPreviewDTO
                Set<Integer> appliedPromotionIds = new HashSet<>();
                for (Promotion promotion : allPromotions) {
                        // Kiểm tra điều kiện áp dụng khuyến mãi
                        boolean checkCondtion = promotion.getPromotionConditions().stream()
                                        .allMatch((promotionCondition) -> promotionCondition
                                                        .getConditionType() == PromotionConditionTypeEnum.PRODUCT_SPECIFIC);

                        boolean checkAction = promotion.getPromotionActions().stream()
                                        .allMatch((promotionAction) -> promotionAction
                                                        .getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT
                                                        || promotionAction
                                                                        .getActionType() == PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT);
                        if (checkCondtion && checkAction) {
                                appliedPromotionIds.add(promotion.getPromotionId());
                                continue;
                        }

                        if (promotionValidator.validateConditions(orderPreviewDTO,
                                        promotion.getPromotionConditions())) {
                                promotionValidator.executeActions(orderPreviewDTO, promotion, checkCondtion);
                                appliedPromotionIds.add(promotion.getPromotionId());
                        } else {
                                throw new ConflictException(
                                                "Promotion " + promotion.getPromotionId() + " cannot be applied");
                        }

                }

                // Cập nhật danh sách mã khuyến mãi đã áp dụng vào OrderPreviewDTO
                orderPreviewDTO.setAppliedPromotions(new ArrayList<>(appliedPromotionIds));

                return orderPreviewDTO;
        }

        @Transactional
        public OrderResponseDTO createOrder_v2(
                        String userName,
                        OrderRequestDTO orderRequestDTO) {

                Customer customer = userValidator.validateAndGetCustomer(userName);

                Address address = null;
                // Kiểm tra địa chỉ
                // for (Address add : customer.getShippingAddresses()) {
                for (int i = 0; i < customer.getShippingAddresses().size(); i++) {
                        if (customer.getShippingAddresses().get(i).getAddressId() == orderRequestDTO
                                        .getAddressShippingId()) {
                                address = customer.getShippingAddresses().get(i);
                                break;
                        }
                        if (i == customer.getShippingAddresses().size() - 1) {
                                throw new NotFoundException("Shipping address not found");
                        }
                }

                // Tạo đối tượng CreatePreviewDTO
                CreatePreviewDTO createPreviewDTO = new CreatePreviewDTO();

                createPreviewDTO.setPromotionIds(orderRequestDTO.getPromotionApplyIds());

                List<CreatePreviewDTO.CreatePreviewDetailsDTO> createPreviewDetailsDTOs = orderRequestDTO
                                .getOrderDetails()
                                .stream()
                                .map((orderDetail) -> new CreatePreviewDetailsDTO(
                                                orderDetail.getProductDetailId(),
                                                orderDetail.getQuantity()))
                                .collect(Collectors.toList());

                createPreviewDTO.setDetails(createPreviewDetailsDTOs);

                // Tạo previewOrder
                OrderPreviewDTO orderPreviewDTO = createPreviewOrder_v2(customer.getUserId(), createPreviewDTO);

                // Từ OrderPreviewDTO, tạo Order và lưu vào database
                Order order = new Order();

                order.setStatus(OrderStatusEnum.PLACED);

                // Giá tiền
                order.setTotalAmount(orderPreviewDTO.getTotalAmount());
                order.setDiscountAmount(orderPreviewDTO.getDiscountAmount());
                order.setShippingFee(orderPreviewDTO.getShippingFee());
                order.setDiscountShippingFee(orderPreviewDTO.getDiscountShippingFee());
                order.setFinalAmount(orderPreviewDTO.getFinalAmount());

                order.setDeliveryDate(null);

                // Thông tin người nhận
                order.setRecipientName(address.getRecipientName());
                order.setPhoneNumber(address.getPhoneNumber());
                order.setDetailedAddress(address.getDetailedAdress());
                order.setWard(address.getWard());
                order.setProvince(address.getProvince());

                // Phương thức thanh toán
                order.setPaymentMethod(orderRequestDTO.getPaymentMethod());
                order.setPaymentStatus(OrderPaymentStatusEnum.UNPAID);
                order.setPaymentId(null);

                order.setIsReview(false);

                // Danh sách chi tiết đơn hàng
                Map<Integer, ProductDetail> productDetails = productDetailRepository.findAllById(
                                orderPreviewDTO.getOrderDetails().stream()
                                                .map((orderDetailPreview) -> orderDetailPreview.getProductDetailId())
                                                .toList())
                                .stream().collect(Collectors.toMap((prodcutDetail -> prodcutDetail.getDetailId()),
                                                prodcutDetail -> prodcutDetail));

                List<OrderDetail> orderDetails = orderPreviewDTO.getOrderDetails()
                                .stream()
                                .map(
                                                (orderDetailPreview) -> {
                                                        OrderDetail orderDetail = new OrderDetail();
                                                        orderDetail.setQuantity(orderDetailPreview.getQuantity());
                                                        orderDetail.setPrice(orderDetailPreview.getPrice());
                                                        orderDetail.setDiscount(orderDetailPreview.getDiscountAmount());
                                                        orderDetail.setFinalPrice(orderDetailPreview.getFinalPrice());
                                                        orderDetail.setProductName(orderDetailPreview.getProductName());
                                                        orderDetail.setProductImage(
                                                                        orderDetailPreview.getProductImage());
                                                        orderDetail.setColor(orderDetailPreview.getColor());
                                                        orderDetail.setSize(orderDetailPreview.getSize());
                                                        orderDetail.setProductDetail(productDetails
                                                                        .get(orderDetailPreview.getProductDetailId()));
                                                        orderDetail.setOrder(order);
                                                        orderDetail.setIsReview(false);

                                                        return orderDetail;
                                                })
                                .toList();

                // Mã khuyến mãi đã áp dụng
                List<Promotion> appliedPromotions = promotionRepository
                                .findAllById(orderPreviewDTO.getAppliedPromotions());

                order.setPromotions(appliedPromotions);
                order.setOrderDetails(orderDetails);
                order.setCustomer(customer);
                order.setRefundRequests(new ArrayList<>());

                // Cập nhật cart nếu orderType là CART
                Map<Integer, OrderDetailRequestDTO> orderDetailRequestMaps = orderRequestDTO.getOrderDetails().stream()
                                .collect(Collectors.toMap(
                                                (detail) -> detail.getProductDetailId(),
                                                (detail) -> detail));

                if (orderRequestDTO.getOrderType() == OrderTypeEnum.CART) {
                        updateCartAfterOrder(customer.getUserId(), orderDetailRequestMaps);
                }

                // Cập nhật voucher
                customer.getVoucherWallets().removeIf(vw -> orderRequestDTO.getPromotionApplyIds()
                                .contains(vw.getPromotion().getPromotionId()));

                customerRepository.save(customer);

                // Cập nhật lại số lượng sản phẩm trong kho
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                        ProductDetail productDetail = orderDetail.getProductDetail();
                        productDetail.setQuantity(productDetail.getQuantity() - orderDetail.getQuantity());
                }

                orderRepository.save(order);

                return orderMapper.toResponseDTO(order);

        }
}
