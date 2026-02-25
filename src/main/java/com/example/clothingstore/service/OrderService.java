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

                // 1. Lấy thông tin customer
                Customer customer = customerRepository.findByUserName(userName)
                                .orElseThrow(() -> new NotFoundException("Customer not found"));

                // 2. Lấy địa chỉ giao hàng
                ShippingAddress shippingAddress = customer.getShippingAddresses()
                                .stream()
                                .filter(addr -> addr.getAddressId().equals(orderRequestDTO.getAddressShippingId()))
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException("Address shipping not found"));

                List<Promotion> promotions = promotionRepository.findAllById(orderRequestDTO.getPromotionApplyIds());

                if (promotions.isEmpty() || promotions.size() != orderRequestDTO.getPromotionApplyIds().size()) {
                        throw new NotFoundException("Some promotion items not found ");
                }

                for (Promotion promotion : promotions) {
                        if (promotion.getIsActive() == false) {
                                throw new ConflictException(
                                                "Promotion with id " + promotion.getPromotionId() + " is not active");
                        }
                }

                // 3. Khởi tạo order
                Order order = new Order();
                order.setCustomer(customer);
                order.setDeliveryDate(LocalDateTime.now().plusDays(3));
                order.setStatus(OrderStatusEnum.PLACED);
                order.setIsReview(false);

                // 4. Set thông tin giao hàng
                order.setRecipientName(shippingAddress.getRecipientName());
                order.setPhoneNumber(shippingAddress.getPhoneNumber());
                order.setDetailedAddress(shippingAddress.getDetailedAdress());
                order.setWard(shippingAddress.getWard());
                order.setProvince(shippingAddress.getProvince());

                List<OrderDetailRequestDTO> orderDetailRequestDTOs = orderRequestDTO.getOrderDetailRequestDTOs();

                Map<Integer, OrderDetailRequestDTO> orderDetailRequestMaps = orderDetailRequestDTOs
                                .stream()
                                .collect(Collectors.toMap(
                                                orderDetailRequestDTO -> orderDetailRequestDTO.getProductDetailId(),
                                                orderDetailRequestDTO -> orderDetailRequestDTO));

                List<ProductDetail> productDetails = productDetailRepository
                                .findAllById(orderDetailRequestMaps.keySet());

                Map<Integer, ProductDetail> productDetailMaps = productDetails
                                .stream()
                                .collect(Collectors.toMap(ProductDetail::getDetailId, pd -> pd));

                if (productDetails.isEmpty() || orderDetailRequestMaps.size() != productDetails.size()) {
                        throw new NotFoundException("Some product details not found");
                }

                Set<OrderDetailPreviewDTO> orderDetailPreviews = productDetails
                                .stream()
                                .filter((pd) -> orderDetailRequestMaps.get(pd.getDetailId()).getIsFree() == false)
                                .map(
                                                (pd) -> {
                                                        // if (orderDetailRequestMaps.get(pd.getDetailId())
                                                        // .getIsFree() == true) {
                                                        // continue;
                                                        // }

                                                        // final Double discountAmount = 0.0;
                                                        final Double discountAmount = pd.getProductColor().getProduct()
                                                                        .getDiscount() != null
                                                                                        ? pd.getProductColor()
                                                                                                        .getProduct()
                                                                                                        .getDiscount()
                                                                                        : 0.0; // Lấy giá trị giảm giá
                                                                                               // từ sản phẩm, nếu không
                                                                                               // có thì mặc định là 0

                                                        final Double price = pd.getProductColor().getProduct()
                                                                        .getUnitPrice();

                                                        final Double finalPrice = price - discountAmount; // Tính giá
                                                                                                          // cuối cùng
                                                                                                          // sau khi áp
                                                                                                          // dụng giảm
                                                                                                          // giá

                                                        final Boolean isFree = false;

                                                        OrderDetailPreviewDTO orderDetailPreviewDTO = OrderDetailPreviewDTO
                                                                        .builder()
                                                                        .productDetailId(pd.getDetailId())
                                                                        .productName(pd.getProductColor().getProduct()
                                                                                        .getProductName())
                                                                        .productImage(pd.getProductColor().getProduct()
                                                                                        .getProductImage())
                                                                        .color(pd.getProductColor().getColor())
                                                                        .size(pd.getSize())
                                                                        .quantity(orderDetailRequestMaps
                                                                                        .get(pd.getDetailId())
                                                                                        .getQuantity())
                                                                        .price(price)
                                                                        .discountAmount(discountAmount)
                                                                        .finalPrice(finalPrice)
                                                                        .isFree(isFree)
                                                                        .build();
                                                        return orderDetailPreviewDTO;
                                                })

                                .collect(Collectors.toSet());

                // ====================================================================

                Double totalAmountPreview = orderDetailPreviews.stream()
                                .mapToDouble(orderDetail -> orderDetail.getFinalPrice() * orderDetail.getQuantity())
                                .sum();

                Double discountAmountPreview = 0.0;

                Double shippingFeePreview = 30000.0;

                Double discountShippingFeePreview = 0.0;

                Double finalAmount = totalAmountPreview - discountAmountPreview + shippingFeePreview
                                - discountShippingFeePreview;

                OrderPreviewDTO orderPreviewDTO = OrderPreviewDTO.builder()
                                .orderDetails(orderDetailPreviews)
                                .totalAmount(totalAmountPreview)
                                .discountAmount(discountAmountPreview)
                                .shippingFee(shippingFeePreview)
                                .discountShippingFee(discountShippingFeePreview)
                                .finalAmount(finalAmount)
                                .appliedPromotions(new ArrayList<>())
                                .build();

                for (Promotion promotion : promotions) {

                        boolean isApplicable = true;

                        for (PromotionCondition promotionCondition : promotion.getPromotionConditions()) {
                                PromotionConditionStrategy promotionConditionStrategy = promotionConditionFactory
                                                .getPromotionConditionStrategy(promotionCondition.getConditionType());

                                Map<String, Object> value = promotionCondition.getValue();

                                if (!promotionConditionStrategy.isSatisfied(orderPreviewDTO, value)) {
                                        isApplicable = false;
                                        break;
                                }
                        }

                        if (isApplicable) {

                                List<PromotionAction> actions = promotion.getPromotionActions();

                                for (PromotionAction action : actions) {

                                        PromotionActionTypeEnum actionType = action.getActionType();

                                        PromotionActionStrategy actionFactory = promotionActionFactory
                                                        .getPromotionActionStrategy(actionType);

                                        actionFactory.execute(orderPreviewDTO, promotion,
                                                        promotion.getPromotionActions().indexOf(action));

                                }
                                orderPreviewDTO.getAppliedPromotions().add(promotion.getPromotionId());

                        }

                }

                // KHỚP LẠI GIÁ TRỊ
                // ====================================================================

                Set<OrderDetailPreviewDTO> orderDetailPreviewDTOCheck = orderPreviewDTO.getOrderDetails();

                // ==== KIỂM TRA GIÁ TRỊ CHI TIẾT ĐƠN HÀNG ====
                // FIX: Sử dụng Objects.equals() thay vì == để tránh floating-point precision
                // issues
                for (OrderDetailPreviewDTO orderDetailPreviewDTO : orderDetailPreviewDTOCheck) {
                        // LỖIC CŨ (không dùng):
                        // boolean matchPrice = orderDetailPreviewDTO.getPrice() ==
                        // orderDetailRequestMaps
                        // .get(orderDetailPreviewDTO.getProductDetailId()).getPrice();
                        // boolean matchDiscount = orderDetailPreviewDTO.getDiscountAmount() ==
                        // orderDetailRequestMaps
                        // .get(orderDetailPreviewDTO.getProductDetailId()).getDiscount();
                        // boolean matchFinalPrice = orderDetailPreviewDTO.getFinalPrice() ==
                        // orderDetailRequestMaps
                        // .get(orderDetailPreviewDTO.getProductDetailId()).getFinalPrice();
                        // boolean matchIsFree = orderDetailPreviewDTO.getIsFree() ==
                        // orderDetailRequestMaps
                        // .get(orderDetailPreviewDTO.getProductDetailId()).getIsFree();

                        // LỖIC ĐÚNG:
                        boolean matchPrice = Objects.equals(orderDetailPreviewDTO.getPrice(),
                                        orderDetailRequestMaps.get(orderDetailPreviewDTO.getProductDetailId())
                                                        .getPrice());
                        boolean matchDiscount = Objects.equals(orderDetailPreviewDTO.getDiscountAmount(),
                                        orderDetailRequestMaps.get(orderDetailPreviewDTO.getProductDetailId())
                                                        .getDiscount());
                        boolean matchFinalPrice = Objects.equals(orderDetailPreviewDTO.getFinalPrice(),
                                        orderDetailRequestMaps.get(orderDetailPreviewDTO.getProductDetailId())
                                                        .getFinalPrice());
                        boolean matchIsFree = Objects.equals(orderDetailPreviewDTO.getIsFree(),
                                        orderDetailRequestMaps.get(orderDetailPreviewDTO.getProductDetailId())
                                                        .getIsFree());

                        if (!matchPrice || !matchDiscount || !matchFinalPrice || !matchIsFree) {
                                throw new ConflictException("Price mismatch for product detail id: "
                                                + orderDetailPreviewDTO.getProductDetailId());
                        }
                }
                // ====

                // ==== KIỂM TRA GIÁ TRỊ TỔNG ĐƠN HÀNG ====
                // FIX: Sửa logic so sánh - phải so sánh orderRequestDTO với orderPreviewDTO,
                // không phải với chính nó
                // LỖIC CŨ (không dùng - so sánh field với chính nó):
                // if (orderRequestDTO.getTotalAmount() != orderRequestDTO.getTotalAmount()
                // || orderRequestDTO.getDiscount() != orderRequestDTO.getDiscount()
                // || orderRequestDTO.getShippingFee() != orderRequestDTO.getShippingFee()
                // || orderRequestDTO.getDiscountShippingFee() !=
                // orderRequestDTO.getDiscountShippingFee()
                // || orderRequestDTO.getFinalAmount() != orderRequestDTO.getFinalAmount()) {
                // throw new ConflictException("Order summary mismatch");
                // }

                // LỖIC ĐÚNG:
                if (!Objects.equals(orderRequestDTO.getTotalAmount(), orderPreviewDTO.getTotalAmount())
                                || !Objects.equals(orderRequestDTO.getDiscount(), orderPreviewDTO.getDiscountAmount())
                                || !Objects.equals(orderRequestDTO.getShippingFee(), orderPreviewDTO.getShippingFee())
                                || !Objects.equals(orderRequestDTO.getDiscountShippingFee(),
                                                orderPreviewDTO.getDiscountShippingFee())
                                || !Objects.equals(orderRequestDTO.getFinalAmount(),
                                                orderPreviewDTO.getFinalAmount())) {
                        throw new ConflictException("Order summary mismatch");
                }
                // ====

                // ====================================================================

                // 7. Tính tổng tiền và set các thông tin khác
                Double totalAmount = orderDetailPreviewDTOCheck.stream()
                                .mapToDouble(od -> od.getFinalPrice() * od.getQuantity())
                                .sum();

                List<OrderDetail> orderDetails = orderDetailPreviewDTOCheck.stream().map((preview) -> {
                        OrderDetail orderDetail = new OrderDetail();

                        // orderDetail.setDetailId(preview.getProductDetailId());
                        orderDetail.setQuantity(preview.getQuantity());
                        orderDetail.setPrice(preview.getPrice());
                        orderDetail.setDiscount(preview.getDiscountAmount());
                        orderDetail.setFinalPrice(preview.getFinalPrice());
                        orderDetail.setProductName(preview.getProductName());
                        orderDetail.setProductImage(preview.getProductImage());
                        orderDetail.setColor(preview.getColor());
                        orderDetail.setSize(preview.getSize());

                        orderDetail.setProductDetail(productDetailMaps.get(preview.getProductDetailId()));
                        orderDetail.setOrder(order);

                        orderDetail.setIsReview(false);
                        return orderDetail;
                }).toList();

                order.setTotalAmount(totalAmount);
                order.setOrderDetails(orderDetails);
                order.setPaymentMethod(orderRequestDTO.getPaymentMethod());
                order.setShippingFee(orderPreviewDTO.getShippingFee());
                // order.setDiscountAmount(0.0);
                order.setDiscountAmount(orderPreviewDTO.getDiscountAmount());
                order.setDiscountShippingFee(orderPreviewDTO.getDiscountShippingFee());
                order.setFinalAmount(orderPreviewDTO.getFinalAmount());

                order.setStatus(OrderStatusEnum.PLACED); // Mặc định khi tạo đơn sẽ có trạng thái là PLACED, không lấy
                                                         // từ client để tránh lỗi
                order.setIsReview(false);

                // 8. Lưu dữ liệu
                productDetailRepository.saveAll(productDetails);
                orderRepository.save(order);

                // 9. Tạo response DTO
                OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                orderResponseDTO.setOrderId(order.getOrderId());
                orderResponseDTO.setTotalAmount(order.getTotalAmount());
                orderResponseDTO.setDiscountAmount(order.getDiscountAmount());
                orderResponseDTO.setShippingFee(order.getShippingFee());
                orderResponseDTO.setDiscountShippingFee(order.getDiscountShippingFee());
                orderResponseDTO.setFinalAmount(order.getFinalAmount());
                orderResponseDTO.setDeliveryDate(order.getDeliveryDate());
                orderResponseDTO.setStatus(order.getStatus());
                orderResponseDTO.setRecipientName(order.getRecipientName());
                orderResponseDTO.setPhoneNumber(order.getPhoneNumber());
                orderResponseDTO.setDetailedAddress(order.getDetailedAddress());
                orderResponseDTO.setWard(order.getWard());
                orderResponseDTO.setProvince(order.getProvince());
                orderResponseDTO.setZaloAppTransId(order.getZaloAppTransId());
                orderResponseDTO.setIsReview(order.getIsReview());

                // 10. Map order detail
                List<OrderDetailResponseDTO> orderDetailResponseDTOs = order.getOrderDetails().stream().map(od -> {
                        OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
                        orderDetailResponseDTO.setProductName(od.getProductName());
                        orderDetailResponseDTO.setProductImage(od.getProductImage());
                        orderDetailResponseDTO.setColor(od.getColor());
                        orderDetailResponseDTO.setSize(od.getSize());
                        orderDetailResponseDTO.setQuantity(od.getQuantity());
                        orderDetailResponseDTO.setPrice(od.getPrice());
                        orderDetailResponseDTO.setOrderDetailId(od.getDetailId());
                        orderDetailResponseDTO.setProductId(
                                        od.getProductDetail().getProductColor().getProduct().getProductId());
                        orderDetailResponseDTO.setIsReview(od.getIsReview());
                        return orderDetailResponseDTO;
                }).toList();
                orderResponseDTO.setOrderDetails(orderDetailResponseDTOs);

                // 11. Map order gift
                // if (order.getOrderGifts() != null) {
                // List<OrderGiftResponseDTO> orderGiftDTOs =
                // order.getOrderGifts().stream().map(og -> {
                // OrderGiftResponseDTO ogDTO = new OrderGiftResponseDTO();
                // ogDTO.setGiftName(og.getGiftName());
                // ogDTO.setGiftQuantity(og.getGiftQuantity());
                // ogDTO.setGiftImage(og.getGiftImage());
                // ogDTO.setPromotionName(og.getPromotionName());
                // return ogDTO;
                // }).toList();
                // orderResponseDTO.setOrderGifts(orderGiftDTOs);
                // }

                // 12. Điều chỉnh cart
                Cart cart = cartRepository.findByCustomer_CustomerId(order.getCustomer().getCustomerId())
                                .orElseThrow(() -> new NotFoundException("Cart not found"));

                cart.getCartItems().removeIf(
                                cartItem -> productDetailMaps.containsKey(cartItem.getProductDetail().getDetailId()));

                cartRepository.save(cart);

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

}
