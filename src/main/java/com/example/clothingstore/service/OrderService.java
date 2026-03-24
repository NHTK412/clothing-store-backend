package com.example.clothingstore.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.OrderTypeEnum;
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

        @Transactional
        public OrderResponseDTO createOrder(String userName, OrderRequestDTO orderRequestDTO) {
                Customer customer = userValidator.validateAndGetCustomer(userName);
                Address shippingAddress = userValidator.validateAndGetShippingAddress(customer,
                                orderRequestDTO.getAddressShippingId());
                List<Promotion> promotions = promotionValidator
                                .validateAndGetPromotions(orderRequestDTO.getPromotionApplyIds());
                List<OrderDetailRequestDTO> orderDetailRequestDTOs = orderRequestDTO.getOrderDetails();
                Map<Integer, OrderDetailRequestDTO> orderDetailRequestMaps = buildOrderDetailRequestMap(
                                orderDetailRequestDTOs);
                Map<Integer, ProductDetail> productDetailMaps = getAndValidateProductDetails(orderDetailRequestDTOs);
                List<ProductDetail> productDetails = new ArrayList<>(productDetailMaps.values());
                Set<OrderDetailPreviewDTO> orderDetailPreviews = orderMapper.toSetDetailPreviewDTO(productDetails,
                                orderDetailRequestMaps);
                OrderPreviewDTO orderPreviewDTO = orderMapper.toPreviewDTO(orderDetailPreviews);
                promotionValidator.applyPromotions(orderPreviewDTO, promotions);
                Set<OrderDetailPreviewDTO> orderDetailPreviewDTOCheck = orderMapper.toSetDetailPreviewDTO(
                                productDetails, orderDetailRequestMaps);
                Order order = orderMapper.toOrder(
                                orderPreviewDTO,
                                shippingAddress,
                                customer,
                                orderRequestDTO,
                                productDetailMaps);

                if (orderRequestDTO.getOrderType() == OrderTypeEnum.CART) {
                        updateCartAfterOrder(customer.getUserId(), orderDetailRequestMaps);
                }
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                        ProductDetail productDetail = orderDetail.getProductDetail();
                        productDetail.setQuantity(productDetail.getQuantity() - orderDetail.getQuantity());
                }
                productDetailRepository.saveAll(productDetails);
                List<Promotion> appliedPromotions = promotionRepository
                                .findAllById(orderPreviewDTO.getAppliedPromotions());
                order.setPromotions(appliedPromotions);
                customer.getVoucherWallets().removeIf(vw -> orderRequestDTO.getPromotionApplyIds()
                                .contains(vw.getPromotion().getPromotionId()));
                orderRepository.save(order);
                OrderResponseDTO orderResponseDTO = orderMapper.toResponseDTO(order);
                return orderResponseDTO;
        }

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

        private Map<Integer, OrderDetailRequestDTO> buildOrderDetailRequestMap(
                        List<OrderDetailRequestDTO> orderDetailRequestDTOs) {
                return orderDetailRequestDTOs.stream()
                                .collect(Collectors.toMap(
                                                OrderDetailRequestDTO::getProductDetailId,
                                                dto -> dto));
        }

        private Map<Integer, ProductDetail> getAndValidateProductDetails(
                        List<OrderDetailRequestDTO> orderDetailRequestDTOs) {
                Set<Integer> productDetailIds = orderDetailRequestDTOs.stream()
                                .map(OrderDetailRequestDTO::getProductDetailId)
                                .collect(Collectors.toSet());
                List<ProductDetail> productDetails = productDetailRepository.findAllById(productDetailIds);
                if (productDetails.isEmpty() || productDetails.size() != productDetailIds.size()) {
                        throw new NotFoundException("Some product details not found");
                }
                return productDetails.stream()
                                .collect(Collectors.toMap(ProductDetail::getDetailId, pd -> pd));
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
}
