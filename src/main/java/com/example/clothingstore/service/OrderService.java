package com.example.clothingstore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.action.PromotionActionFactory;
import com.example.clothingstore.action.PromotionActionStrategy;
import com.example.clothingstore.condition.PromotionConditionFactory;
import com.example.clothingstore.condition.PromotionConditionStrategy;
import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.order.OrderRequestDTO;
import com.example.clothingstore.dto.order.OrderResponseDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailRequestDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailResponseDTO;
import com.example.clothingstore.dto.ordergift.OrderGiftResponseDTO;
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.PaymentMethodEnum;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.customer.ConflictException;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.model.Cart;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.model.OrderDetail;
import com.example.clothingstore.model.OrderGift;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.model.PromotionAction;
import com.example.clothingstore.model.PromotionCondition;
import com.example.clothingstore.model.ShippingAddress;
import com.example.clothingstore.repository.CartRepository;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.OrderRepository;
import com.example.clothingstore.repository.ProductDetailRepository;
import com.example.clothingstore.repository.PromotionRepository;

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

        private final PromotionConditionFactory promotionConditionFactory;
        private final PromotionActionFactory promotionActionFactory;

        @Transactional
        public OrderResponseDTO createOrder(String userName, OrderRequestDTO orderRequestDTO) {
                // 1. Lấy thông tin customer và địa chỉ giao hàng
                Customer customer = validateAndGetCustomer(userName);
                ShippingAddress shippingAddress = validateAndGetShippingAddress(customer,
                                orderRequestDTO.getAddressShippingId());

                // 2. Lấy và xác thực promotions
                List<Promotion> promotions = validateAndGetPromotions(orderRequestDTO.getPromotionApplyIds());

                // 3. Khởi tạo order
                Order order = initializeOrder(customer);
                setOrderShippingInfo(order, shippingAddress);

                // 4. Lấy chi tiết sản phẩm
                List<OrderDetailRequestDTO> orderDetailRequestDTOs = orderRequestDTO.getOrderDetailRequestDTOs();
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
                validateOrderPrices(orderPreviewDTO.getOrderDetails(), orderPreviewDTO,
                                orderRequestDTO, orderDetailRequestMaps);

                // 8. Tạo order details và set thông tin cuối cùng
                Set<OrderDetailPreviewDTO> orderDetailPreviewDTOCheck = orderPreviewDTO.getOrderDetails();
                List<OrderDetail> orderDetails = createOrderDetailEntities(orderDetailPreviewDTOCheck,
                                productDetailMaps, order);
                order.setOrderDetails(orderDetails);

                setOrderFinalInfo(order, orderPreviewDTO, orderRequestDTO);

                // 9. Lưu dữ liệu
                productDetailRepository.saveAll(productDetails);
                orderRepository.save(order);

                // 10. Tạo response DTO
                OrderResponseDTO orderResponseDTO = createOrderResponseDTO(order);

                // 11. Cập nhật cart
                updateCartAfterOrder(customer.getCustomerId(), orderDetailRequestMaps);

                return orderResponseDTO;
        }

        @Transactional
        public OrderResponseDTO getOrderById(Integer orderId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));

                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

                orderResponseDTO.setOrderId(order.getOrderId());

                orderResponseDTO.setTotalAmount(order.getTotalAmount());

                orderResponseDTO.setDiscountAmount(order.getDiscountAmount());

                orderResponseDTO.setShippingFee(order.getShippingFee());

                orderResponseDTO.setDeliveryDate(order.getDeliveryDate());

                orderResponseDTO.setStatus(order.getStatus());

                orderResponseDTO.setRecipientName(order.getRecipientName());

                orderResponseDTO.setPhoneNumber(order.getPhoneNumber());

                orderResponseDTO.setDetailedAddress(order.getDetailedAddress());

                orderResponseDTO.setWard(order.getWard());

                orderResponseDTO.setProvince(order.getProvince());

                // orderResponseDTO.setPayment(order.getPaymentMethod());

                // orderResponseDTO.setPaymentStatus(order.getPaymentStatus());

                orderResponseDTO.setZaloAppTransId(order.getZaloAppTransId());

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

                                        orderDetailResponseDTO.setProductId(od.getProductDetail().getProductColor()
                                                        .getProduct().getProductId());

                                        return orderDetailResponseDTO;
                                })
                                .toList();

                orderResponseDTO.setOrderDetails(orderDetailResponseDTOs);

                // Map order gift
                // List<OrderGiftResponseDTO> orderGiftDTOs = order.getOrderGifts()
                // .stream()
                // .map(og -> {
                // OrderGiftResponseDTO ogDTO = new OrderGiftResponseDTO();

                // ogDTO.setGiftName(og.getGiftName());

                // ogDTO.setGiftQuantity(og.getGiftQuantity());

                // ogDTO.setGiftImage(og.getGiftImage());

                // ogDTO.setPromotionName(og.getPromotionName());

                // return ogDTO;
                // })
                // .toList();
                // orderResponseDTO.setOrderGifts(orderGiftDTOs);

                return orderResponseDTO;
        }

        @Transactional
        public List<OrderSummaryDTO> getAllOrdersByCustomer(Integer customerId, Pageable pageable) {

                Page<Order> orders = orderRepository.findAllByCustomerId(customerId, pageable);

                List<OrderSummaryDTO> orderSummaries = orders
                                .stream()
                                .map(order -> {

                                        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();

                                        orderSummaryDTO.setOrderId(order.getOrderId());

                                        orderSummaryDTO.setTotalAmount(order.getTotalAmount());

                                        orderSummaryDTO.setShippingFee(order.getShippingFee());

                                        orderSummaryDTO.setDeliveryDate(order.getDeliveryDate());

                                        orderSummaryDTO.setStatus(order.getStatus());

                                        orderSummaryDTO.setOrderFirstName(
                                                        order.getOrderDetails().get(0).getProductName());

                                        orderSummaryDTO.setOrderFirstImage(
                                                        order.getOrderDetails().get(0).getProductImage());

                                        orderSummaryDTO.setOrderQuantity(order.getOrderDetails().size());

                                        // orderSummaryDTO.setPaymentStatus(order.getPaymentStatus());

                                        return orderSummaryDTO;
                                })
                                .toList();

                return orderSummaries;
        }

        @Transactional
        public List<OrderSummaryDTO> getAllOrders(Pageable pageable) {

                Page<Order> orders = orderRepository.findAll(pageable);

                List<OrderSummaryDTO> orderSummaries = orders
                                .stream()
                                .map(order -> {

                                        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();

                                        orderSummaryDTO.setOrderId(order.getOrderId());

                                        orderSummaryDTO.setTotalAmount(order.getTotalAmount());

                                        orderSummaryDTO.setShippingFee(order.getShippingFee());

                                        orderSummaryDTO.setDeliveryDate(order.getDeliveryDate());

                                        orderSummaryDTO.setStatus(order.getStatus());

                                        orderSummaryDTO.setOrderFirstName(
                                                        order.getOrderDetails().get(0).getProductName());

                                        orderSummaryDTO.setOrderFirstImage(
                                                        order.getOrderDetails().get(0).getProductImage());

                                        orderSummaryDTO.setOrderQuantity(order.getOrderDetails().size());

                                        // orderSummaryDTO.setPaymentStatus(order.getPaymentStatus());

                                        return orderSummaryDTO;
                                })
                                .toList();

                return orderSummaries;
        }

        @Transactional
        public OrderResponseDTO updateStatus(Integer orderId, OrderStatusEnum status) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));

                if (status == OrderStatusEnum.CANCELED) {
                        if (order.getStatus() != OrderStatusEnum.PLACED) {
                                throw new ConflictException("Only orders with status PLACED can be canceled");
                        }

                        // Hoàn lại số lượng vào kho
                        for (OrderDetail od : order.getOrderDetails()) {
                                ProductDetail pd = od.getProductDetail();
                                pd.setQuantity(pd.getQuantity() + od.getQuantity());
                                productDetailRepository.save(pd);
                        }
                }

                order.setStatus(status);
                orderRepository.save(order);

                return getOrderById(order.getOrderId());
        }

        private Customer validateAndGetCustomer(String userName) {
                return customerRepository.findByUserName(userName)
                                .orElseThrow(() -> new NotFoundException("Customer not found"));
        }

        private ShippingAddress validateAndGetShippingAddress(Customer customer, Integer addressId) {
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
                order.setDeliveryDate(LocalDateTime.now().plusDays(3));
                order.setStatus(OrderStatusEnum.PLACED);
                order.setIsReview(false);
                return order;
        }

        private void setOrderShippingInfo(Order order, ShippingAddress shippingAddress) {
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
                                .filter(pd -> orderDetailRequestMap.get(pd.getDetailId()).getIsFree() == false)
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

        private void validateOrderPrices(Set<OrderDetailPreviewDTO> orderDetailPreviews,
                        OrderPreviewDTO orderPreview, OrderRequestDTO orderRequest,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                // Xác thực chi tiết từng product
                for (OrderDetailPreviewDTO preview : orderDetailPreviews) {
                        validateOrderDetailPrice(preview, orderDetailRequestMap);
                }

                // Xác thực tổng đơn hàng
                validateOrderSummary(orderRequest, orderPreview);
        }

        private void validateOrderDetailPrice(OrderDetailPreviewDTO preview,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                OrderDetailRequestDTO request = orderDetailRequestMap.get(preview.getProductDetailId());

                boolean matchPrice = Objects.equals(preview.getPrice(), request.getPrice());
                boolean matchDiscount = Objects.equals(preview.getDiscountAmount(), request.getDiscount());
                boolean matchFinalPrice = Objects.equals(preview.getFinalPrice(), request.getFinalPrice());
                boolean matchIsFree = Objects.equals(preview.getIsFree(), request.getIsFree());

                if (!matchPrice || !matchDiscount || !matchFinalPrice || !matchIsFree) {
                        throw new ConflictException(
                                        "Price mismatch for product detail id: " + preview.getProductDetailId());
                }
        }

        private void validateOrderSummary(OrderRequestDTO orderRequest, OrderPreviewDTO orderPreview) {
                if (!Objects.equals(orderRequest.getTotalAmount(), orderPreview.getTotalAmount())
                                || !Objects.equals(orderRequest.getDiscount(), orderPreview.getDiscountAmount())
                                || !Objects.equals(orderRequest.getShippingFee(), orderPreview.getShippingFee())
                                || !Objects.equals(orderRequest.getDiscountShippingFee(),
                                                orderPreview.getDiscountShippingFee())
                                || !Objects.equals(orderRequest.getFinalAmount(),
                                                orderPreview.getFinalAmount())) {
                        throw new ConflictException("Order summary mismatch");
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
                responseDTO.setDeliveryDate(order.getDeliveryDate());
                responseDTO.setStatus(order.getStatus());
                responseDTO.setRecipientName(order.getRecipientName());
                responseDTO.setPhoneNumber(order.getPhoneNumber());
                responseDTO.setDetailedAddress(order.getDetailedAddress());
                responseDTO.setWard(order.getWard());
                responseDTO.setProvince(order.getProvince());
                responseDTO.setZaloAppTransId(order.getZaloAppTransId());
                responseDTO.setIsReview(order.getIsReview());

                // Map order details
                List<OrderDetailResponseDTO> orderDetailDTOs = order.getOrderDetails().stream()
                                .map(this::buildOrderDetailResponseDTO)
                                .toList();
                responseDTO.setOrderDetails(orderDetailDTOs);

                return responseDTO;
        }

        private OrderDetailResponseDTO buildOrderDetailResponseDTO(OrderDetail orderDetail) {
                OrderDetailResponseDTO dto = new OrderDetailResponseDTO();
                dto.setProductName(orderDetail.getProductName());
                dto.setProductImage(orderDetail.getProductImage());
                dto.setColor(orderDetail.getColor());
                dto.setSize(orderDetail.getSize());
                dto.setQuantity(orderDetail.getQuantity());
                dto.setPrice(orderDetail.getPrice());
                dto.setOrderDetailId(orderDetail.getDetailId());
                dto.setProductId(orderDetail.getProductDetail().getProductColor()
                                .getProduct().getProductId());
                dto.setIsReview(orderDetail.getIsReview());
                return dto;
        }

        private void updateCartAfterOrder(Integer customerId,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                                .orElseThrow(() -> new NotFoundException("Cart not found"));

                cart.getCartItems().removeIf(cartItem -> {
                        Integer productDetailId = cartItem.getProductDetail().getDetailId();
                        return orderDetailRequestMap.containsKey(productDetailId)
                                        && !orderDetailRequestMap.get(productDetailId).getIsFree();
                });

                cartRepository.save(cart);
        }

}
