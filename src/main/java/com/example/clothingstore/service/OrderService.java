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

        @Transactional
        public OrderResponseDTO createOrder(String userName, OrderRequestDTO orderRequestDTO) {
                // 1. Lấy thông tin customer và địa chỉ giao hàng
                Customer customer = validateAndGetCustomer(userName);
                Address shippingAddress = validateAndGetShippingAddress(customer,
                                orderRequestDTO.getAddressShippingId());

                // 2. Lấy và xác thực promotions
                List<Promotion> promotions = validateAndGetPromotions(orderRequestDTO.getPromotionApplyIds());

                // 3. Khởi tạo order
                Order order = initializeOrder(customer);
                setOrderShippingInfo(order, shippingAddress);

                // 4. Lấy chi tiết sản phẩm
                List<OrderDetailRequestDTO> orderDetailRequestDTOs = orderRequestDTO.getOrderDetails();
                Map<Integer, OrderDetailRequestDTO> orderDetailRequestMaps = buildOrderDetailRequestMap(
                                orderDetailRequestDTOs);
                Map<Integer, ProductDetail> productDetailMaps = getAndValidateProductDetails(orderDetailRequestDTOs);

                // 5. Tạo preview chi tiết đơn hàng
                List<ProductDetail> productDetails = new ArrayList<>(productDetailMaps.values());
                Set<OrderDetailPreviewDTO> orderDetailPreviews = createOrderDetailPreviews(productDetails,
                                orderDetailRequestMaps);

                // 6. Tạo preview đơn hàng và áp dụng promotions
                OrderPreviewDTO orderPreviewDTO = createOrderPreviewDTO(orderDetailPreviews);
                applyPromotions(orderPreviewDTO, promotions);

                // 7. Xác thực giá cả
                // validateOrderPrices(orderPreviewDTO.getOrderDetails(), orderPreviewDTO,
                // orderRequestDTO, orderDetailRequestMaps);

                // 8. Tạo order details và set thông tin cuối cùng
                Set<OrderDetailPreviewDTO> orderDetailPreviewDTOCheck = orderPreviewDTO.getOrderDetails();
                List<OrderDetail> orderDetails = createOrderDetailEntities(
                                orderDetailPreviewDTOCheck,
                                productDetailMaps,
                                order);
                order.setOrderDetails(orderDetails);

                setOrderFinalInfo(order, orderPreviewDTO, orderRequestDTO);

                // 9. Lưu dữ liệu
                productDetailRepository.saveAll(productDetails);

                order.setPaymentStatus(OrderPaymentStatusEnum.UNPAID);

                orderRepository.save(order);

                // 10. Tạo response DTO
                OrderResponseDTO orderResponseDTO = createOrderResponseDTO(order);

                // 11. Cập nhật cart
                if (orderRequestDTO.getOrderType() == OrderTypeEnum.CART) {
                        updateCartAfterOrder(customer.getUserId(), orderDetailRequestMaps);
                }

                // 12. Update số lượng sản phẩm trong kho
                for (OrderDetail orderDetail : orderDetails) {
                        ProductDetail productDetail = orderDetail.getProductDetail();
                        productDetail.setQuantity(productDetail.getQuantity() - orderDetail.getQuantity());
                }
                productDetailRepository.saveAll(productDetails);

                List<Promotion> appliedPromotions = promotionRepository
                                .findAllById(orderPreviewDTO.getAppliedPromotions());

                order.setPromotions(appliedPromotions);

                customer.getVoucherWallets().removeIf(vw -> orderRequestDTO.getPromotionApplyIds()
                                .contains(vw.getPromotion().getPromotionId()));

                return orderResponseDTO;
        }

        @Transactional
        public OrderResponseDTO getOrderById(Integer orderId, Integer userId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));

                User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

                if (user.getRole() == RoleEnum.ROLE_CUSTOMER) {
                        if (!order.getCustomer().getUserId().equals(userId)) {
                                throw new ConflictException("You can only view your own orders");
                        }
                }

                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                orderResponseDTO.setOrderId(order.getOrderId());
                orderResponseDTO.setTotalAmount(order.getTotalAmount());
                orderResponseDTO.setDiscountAmount(order.getDiscountAmount());
                orderResponseDTO.setShippingFee(order.getShippingFee());
                orderResponseDTO.setDiscountShippingFee(order.getDiscountShippingFee());
                orderResponseDTO.setFinalAmount(order.getFinalAmount());
                // orderResponseDTO.setDeliveryDate(order.getDeliveryDate());
                orderResponseDTO.setStatus(order.getStatus());
                orderResponseDTO.setRecipientName(order.getRecipientName());
                orderResponseDTO.setPhoneNumber(order.getPhoneNumber());
                orderResponseDTO.setDetailedAddress(order.getDetailedAddress());
                orderResponseDTO.setWard(order.getWard());
                orderResponseDTO.setProvince(order.getProvince());

                orderResponseDTO.setPaymentMethod(order.getPaymentMethod());
                orderResponseDTO.setPaymentStatus(order.getPaymentStatus());
                orderResponseDTO.setPaymentId(order.getPaymentId());

                // Map order detail
                List<OrderDetailResponseDTO> orderDetailResponseDTOs = order.getOrderDetails()
                                .stream()
                                .map(od -> {
                                        OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
                                        orderDetailResponseDTO.setProductName(od.getProductName());
                                        orderDetailResponseDTO.setProductImage(od.getProductImage());
                                        orderDetailResponseDTO.setColor(od.getColor());
                                        orderDetailResponseDTO.setSize(od.getSize());
                                        orderDetailResponseDTO.setQuantity(od.getQuantity());
                                        orderDetailResponseDTO.setPrice(od.getPrice());
                                        orderDetailResponseDTO.setOrderDetailId(od.getDetailId());
                                        orderDetailResponseDTO.setProductId(
                                                        od.getProductDetail().getProductColor()
                                                                        .getProduct().getProductId());
                                        orderDetailResponseDTO.setDiscount(od.getDiscount());
                                        orderDetailResponseDTO.setFinalPrice(od.getFinalPrice());
                                        orderDetailResponseDTO.setIsReview(od.getIsReview());
                                        orderDetailResponseDTO.setRefundQuantity(refundRequestRepository
                                                        .findByOrder_OrderId(order.getOrderId())
                                                        .stream()
                                                        .flatMap(rr -> rr.getRefundItems().stream())
                                                        .filter(ri -> ri.getProductDetail().getDetailId()
                                                                        .equals(od.getProductDetail().getDetailId()))
                                                        .mapToInt(ri -> ri.getQuantity())
                                                        .sum());
                                        return orderDetailResponseDTO;
                                })
                                .toList();

                orderResponseDTO.setOrderDetails(orderDetailResponseDTOs);
                return orderResponseDTO;
        }

        @Transactional
        public Page<OrderSummaryDTO> getAllOrdersByCustomer(Integer customerId, Pageable pageable) {

                Page<Order> orders = orderRepository.findAllByCustomerId(customerId, pageable);

                return orders.map(
                                order -> {

                                        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();

                                        orderSummaryDTO.setOrderId(order.getOrderId());

                                        orderSummaryDTO.setFinalAmount(order.getFinalAmount());

                                        orderSummaryDTO.setShippingFee(order.getShippingFee());

                                        // orderSummaryDTO.setDeliveryDate(order.getDeliveryDate());

                                        orderSummaryDTO.setStatus(order.getStatus());

                                        orderSummaryDTO.setOrderFirstName(
                                                        order.getOrderDetails().get(0).getProductName());

                                        orderSummaryDTO.setOrderFirstImage(
                                                        order.getOrderDetails().get(0).getProductImage());

                                        orderSummaryDTO.setOrderQuantity(order.getOrderDetails().size());

                                        // orderSummaryDTO.setPaymentStatus(order.getPaymentStatus());

                                        return orderSummaryDTO;
                                });

                // return orderSummaries;
        }

        @Transactional
        public Page<OrderSummaryDTO> getAllOrders(Pageable pageable) {

                Page<Order> orders = orderRepository.findAll(pageable);

                return orders.map(
                                order -> {
                                        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();
                                        orderSummaryDTO.setOrderId(order.getOrderId());
                                        orderSummaryDTO.setFinalAmount(order.getFinalAmount());
                                        orderSummaryDTO.setShippingFee(order.getShippingFee());
                                        // orderSummaryDTO.setDeliveryDate(order.getDeliveryDate());
                                        orderSummaryDTO.setStatus(order.getStatus());
                                        orderSummaryDTO.setOrderFirstName(
                                                        order.getOrderDetails().get(0).getProductName());
                                        orderSummaryDTO.setOrderFirstImage(
                                                        order.getOrderDetails().get(0).getProductImage());
                                        orderSummaryDTO.setOrderQuantity(order.getOrderDetails().size());
                                        return orderSummaryDTO;
                                });
        }

        @Transactional
        public OrderResponseDTO updateStatus(Integer orderId, OrderStatusEnum status) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));

                order.setStatus(status);

                if (status == OrderStatusEnum.CANCELED) {
                        refundProduct(order);
                        refundVoucher(order);
                }

                orderRepository.save(order);

                return getOrderById(order.getOrderId(), order.getCustomer().getUserId());

        }

        private Customer validateAndGetCustomer(String userName) {
                return customerRepository.findByUserName(userName)
                                .orElseThrow(() -> new NotFoundException("Customer not found"));
        }

        private Address validateAndGetShippingAddress(Customer customer, Integer addressId) {
                return customer.getShippingAddresses()
                                .stream()
                                .filter(addr -> addr.getAddressId().equals(addressId))
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException("Address shipping not found"));
        }

        private List<Promotion> validateAndGetPromotions(List<Integer> promotionIds) {
                List<Promotion> promotions = promotionRepository.findAllById(promotionIds);

                if (promotions.isEmpty() || promotions.size() != promotionIds.size()) {
                        throw new NotFoundException("Some promotion items not found");
                }

                // Kiểm tra tất cả promotions đều active
                for (Promotion promotion : promotions) {
                        if (promotion.getIsActive() == false) {
                                throw new ConflictException(
                                                "Promotion with id " + promotion.getPromotionId() + " is not active");
                        }
                }

                return promotions;
        }

        private Order initializeOrder(Customer customer) {
                Order order = new Order();
                order.setCustomer(customer);
                // order.setDeliveryDate(LocalDateTime.now().plusDays(3));
                order.setStatus(OrderStatusEnum.PLACED);
                order.setIsReview(false);
                return order;
        }

        private void setOrderShippingInfo(Order order, Address shippingAddress) {
                order.setRecipientName(shippingAddress.getRecipientName());
                order.setPhoneNumber(shippingAddress.getPhoneNumber());
                order.setDetailedAddress(shippingAddress.getDetailedAdress());
                order.setWard(shippingAddress.getWard());
                order.setProvince(shippingAddress.getProvince());
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

        private Set<OrderDetailPreviewDTO> createOrderDetailPreviews(List<ProductDetail> productDetails,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                return productDetails.stream()
                                // .filter(pd -> orderDetailRequestMap.get(pd.getDetailId()).getIsFree() ==
                                // false)
                                .map(pd -> buildOrderDetailPreviewDTO(pd,
                                                orderDetailRequestMap.get(pd.getDetailId())))
                                .collect(Collectors.toSet());
        }

        private OrderDetailPreviewDTO buildOrderDetailPreviewDTO(ProductDetail productDetail,
                        OrderDetailRequestDTO orderDetailRequest) {
                Double discountAmount = productDetail.getProductColor().getProduct().getDiscount() != null
                                ? productDetail.getProductColor().getProduct().getDiscount()
                                : 0.0;

                Double price = productDetail.getProductColor().getProduct().getUnitPrice();
                Double finalPrice = price - discountAmount;

                return OrderDetailPreviewDTO.builder()
                                .productDetailId(productDetail.getDetailId())
                                .productName(productDetail.getProductColor().getProduct().getProductName())
                                .productImage(productDetail.getProductColor().getProduct().getProductImage())
                                .color(productDetail.getProductColor().getColor())
                                .size(productDetail.getSize())
                                .quantity(orderDetailRequest.getQuantity())
                                .price(price)
                                .discountAmount(discountAmount)
                                .finalPrice(finalPrice)
                                .isFree(false)
                                .build();
        }

        private OrderPreviewDTO createOrderPreviewDTO(Set<OrderDetailPreviewDTO> orderDetailPreviews) {
                Double totalAmount = orderDetailPreviews.stream()
                                .mapToDouble(od -> od.getFinalPrice() * od.getQuantity())
                                .sum();

                Double shippingFee = 30000.0;

                Double finalAmount = totalAmount + shippingFee;

                return OrderPreviewDTO.builder()
                                .orderDetails(orderDetailPreviews)
                                .totalAmount(totalAmount)
                                .discountAmount(0.0)
                                .shippingFee(shippingFee)
                                .discountShippingFee(0.0)
                                .finalAmount(finalAmount)
                                .appliedPromotions(new ArrayList<>())
                                .build();
        }

        private void applyPromotions(OrderPreviewDTO orderPreview, List<Promotion> promotions) {
                for (Promotion promotion : promotions) {
                        if (isPromotionApplicable(orderPreview, promotion)) {
                                executePromotionActions(orderPreview, promotion);
                                orderPreview.getAppliedPromotions().add(promotion.getPromotionId());
                        }
                }
        }

        private boolean isPromotionApplicable(OrderPreviewDTO orderPreview, Promotion promotion) {
                for (PromotionCondition promotionCondition : promotion.getPromotionConditions()) {
                        PromotionConditionStrategy conditionStrategy = promotionConditionFactory
                                        .getPromotionConditionStrategy(promotionCondition.getConditionType());

                        if (!conditionStrategy.isSatisfied(orderPreview, promotionCondition.getValue())) {
                                return false;
                        }
                }
                return true;
        }

        private void executePromotionActions(OrderPreviewDTO orderPreview, Promotion promotion) {
                List<PromotionAction> actions = promotion.getPromotionActions();

                for (int index = 0; index < actions.size(); index++) {
                        PromotionAction action = actions.get(index);
                        PromotionActionStrategy actionStrategy = promotionActionFactory
                                        .getPromotionActionStrategy(action.getActionType());
                        actionStrategy.execute(orderPreview, promotion, index);
                }
        }

        private List<OrderDetail> createOrderDetailEntities(Set<OrderDetailPreviewDTO> orderDetailPreviews,
                        Map<Integer, ProductDetail> productDetailMap, Order order) {
                return orderDetailPreviews.stream()
                                .map(preview -> buildOrderDetailEntity(preview, productDetailMap, order))
                                .toList();
        }

        private OrderDetail buildOrderDetailEntity(OrderDetailPreviewDTO preview,
                        Map<Integer, ProductDetail> productDetailMap, Order order) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setQuantity(preview.getQuantity());
                orderDetail.setPrice(preview.getPrice());
                orderDetail.setDiscount(preview.getDiscountAmount());
                orderDetail.setFinalPrice(preview.getFinalPrice());
                orderDetail.setProductName(preview.getProductName());
                orderDetail.setProductImage(preview.getProductImage());
                orderDetail.setColor(preview.getColor());
                orderDetail.setSize(preview.getSize());
                orderDetail.setProductDetail(productDetailMap.get(preview.getProductDetailId()));
                orderDetail.setOrder(order);
                orderDetail.setIsReview(false);
                return orderDetail;
        }

        private void setOrderFinalInfo(Order order, OrderPreviewDTO orderPreview,
                        OrderRequestDTO orderRequest) {
                order.setPaymentMethod(orderRequest.getPaymentMethod());
                order.setShippingFee(orderPreview.getShippingFee());
                order.setDiscountAmount(orderPreview.getDiscountAmount());
                order.setDiscountShippingFee(orderPreview.getDiscountShippingFee());
                order.setFinalAmount(orderPreview.getFinalAmount());
                order.setStatus(OrderStatusEnum.PLACED);
                order.setIsReview(false);
        }

        private OrderResponseDTO createOrderResponseDTO(Order order) {
                OrderResponseDTO responseDTO = new OrderResponseDTO();
                responseDTO.setOrderId(order.getOrderId());

                responseDTO.setTotalAmount(order.getTotalAmount());
                responseDTO.setDiscountAmount(order.getDiscountAmount());
                responseDTO.setShippingFee(order.getShippingFee());
                responseDTO.setDiscountShippingFee(order.getDiscountShippingFee());
                responseDTO.setFinalAmount(order.getFinalAmount());

                // responseDTO.setDeliveryDate(order.getDeliveryDate());
                responseDTO.setStatus(order.getStatus());
                responseDTO.setRecipientName(order.getRecipientName());
                responseDTO.setPhoneNumber(order.getPhoneNumber());
                responseDTO.setDetailedAddress(order.getDetailedAddress());
                responseDTO.setWard(order.getWard());
                responseDTO.setProvince(order.getProvince());
                // responseDTO.setZaloAppTransId(order.getZaloAppTransId());
                responseDTO.setIsReview(order.getIsReview());

                responseDTO.setPaymentMethod(order.getPaymentMethod());
                responseDTO.setPaymentStatus(order.getPaymentStatus());
                responseDTO.setPaymentId(order.getPaymentId());

                // Map order details
                List<OrderDetailResponseDTO> orderDetailDTOs = order.getOrderDetails().stream()
                                .map(this::buildOrderDetailResponseDTO)
                                .toList();
                responseDTO.setOrderDetails(orderDetailDTOs);

                return responseDTO;
        }

        private OrderDetailResponseDTO buildOrderDetailResponseDTO(OrderDetail orderDetail) {
                OrderDetailResponseDTO dto = new OrderDetailResponseDTO();
                dto.setProductId(orderDetail.getProductDetail().getProductColor()
                                .getProduct().getProductId());
                dto.setProductName(orderDetail.getProductName());
                dto.setProductImage(orderDetail.getProductImage());
                dto.setColor(orderDetail.getColor());
                dto.setSize(orderDetail.getSize());
                dto.setQuantity(orderDetail.getQuantity());

                dto.setPrice(orderDetail.getPrice());
                dto.setDiscount(orderDetail.getDiscount());
                dto.setFinalPrice(orderDetail.getFinalPrice());

                dto.setOrderDetailId(orderDetail.getDetailId());

                dto.setIsReview(orderDetail.getIsReview());

                List<RefundRequest> refundRequests = refundRequestRepository
                                .findByOrder_OrderId(orderDetail.getOrder().getOrderId());

                Integer refundQuantity = refundRequests.stream()
                                .flatMap(rr -> rr.getRefundItems().stream())
                                .filter(ri -> ri.getProductDetail().getDetailId()
                                                .equals(orderDetail.getProductDetail().getDetailId()))
                                .mapToInt(ri -> ri.getQuantity())
                                .sum();

                dto.setRefundQuantity(refundQuantity);

                return dto;
        }

        private void updateCartAfterOrder(
                        Integer customerId,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                Cart cart = cartRepository.findByCustomer_UserId(customerId)
                                .orElseThrow(() -> new NotFoundException("Cart not found"));

                cart.getCartItems().removeIf(cartItem -> {
                        Integer productDetailId = cartItem.getProductDetail().getDetailId();
                        return orderDetailRequestMap.containsKey(productDetailId);
                        // return orderDetailRequestMap.containsKey(productDetailId)
                        // && !orderDetailRequestMap.get(productDetailId).getIsFree();
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
                if (order.getStatus() != OrderStatusEnum.PLACED) {
                        throw new ConflictException("Only orders with status PLACED can be canceled");
                }
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
