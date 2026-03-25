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

        @Transactional
        public OrderPreviewDTO previewOrder(Integer customerId, CreatePreviewDTO createPreviewDTO) {

                // productDetailIdToQuantity: dùng để lưu số lượng sản phẩm tương ứng với từng
                // productDetailId mà khách hàng muốn đặt hàng, được lấy từ createPreviewDTO
                Map<Integer, Integer> productDetailIdToQuantity = createPreviewDTO.getDetails().stream()
                                .collect(
                                                Collectors.toMap(
                                                                CreatePreviewDTO.CreatePreviewDetailsDTO::getProductDetailIds,
                                                                CreatePreviewDTO.CreatePreviewDetailsDTO::getQuantity));

                // productDetails: dùng để lưu thông tin chi tiết của các sản phẩm mà khách hàng
                // muốn đặt hàng, được lấy từ productDetailRepository dựa trên
                // productDetailIdToQuantity
                List<ProductDetail> productDetails = productDetailRepository
                                .findAllById(productDetailIdToQuantity.keySet());

                // isStockEnough: dùng để kiểm tra xem có đủ hàng cho tất cả các sản phẩm không
                boolean isStockEnough = productDetails.stream()
                                .allMatch(productDetail -> productDetail.getQuantity() >= productDetailIdToQuantity
                                                .get(productDetail.getDetailId()));

                if (!isStockEnough) {
                        throw new ConflictException("Some product details do not have enough stock");
                }

                // orderDetails: dùng để lưu thông tin chi tiết của các sản phẩm trong đơn hàng,
                // được tạo từ productDetails và productDetailIdToQuantity, sau đó sẽ được sử
                // dụng để tính toán tổng tiền, giảm giá, v.v. trong orderPreviewDTO
                Set<OrderDetailPreviewDTO> orderDetails = productDetails.stream()
                                .map(productDetail -> {

                                        final Double discountAmount = productDetail.getProductColor().getProduct()
                                                        .getDiscount() != null
                                                                        ? (productDetail.getProductColor()
                                                                                        .getProduct()
                                                                                        .getDiscount())
                                                                        : 0.0;

                                        final Double price = productDetail.getProductColor().getProduct()
                                                        .getUnitPrice();

                                        final Double finalPrice = price - discountAmount;

                                        final Boolean isFree = false;

                                        OrderDetailPreviewDTO orderDetailPreviewDTO = OrderDetailPreviewDTO.builder()
                                                        .productDetailId(productDetail.getDetailId())
                                                        .productName(productDetail.getProductColor().getProduct()
                                                                        .getProductName())
                                                        .productImage(productDetail.getProductColor().getProductImage())
                                                        .color(productDetail.getProductColor().getColor())
                                                        .size(productDetail.getSize())
                                                        .quantity(productDetailIdToQuantity
                                                                        .get(productDetail.getDetailId()))
                                                        .price(price)
                                                        .discountAmount(discountAmount)
                                                        .finalPrice(finalPrice)
                                                        .isFree(isFree)
                                                        .build();
                                        return orderDetailPreviewDTO;
                                }).collect(Collectors.toSet());

                Double totalAmount = orderDetails.stream()
                                .mapToDouble(orderDetail -> orderDetail.getFinalPrice() * orderDetail.getQuantity())
                                .sum();

                // Tính tổng số tiền giảm giá từ các chiến lược khuyến mãi áp dụng
                Double discountAmount = 0.0;

                // Tính phí vận chuyển
                Double shippingFee = 30000.0;

                // Phí giảm vận chuyển ban đầu bằng 0 và sẽ được cập nhật nếu có chiến lược
                // khuyến mãi nào áp dụng giảm phí vận chuyển
                Double discountShippingFee = 0.0;

                Double finalAmount = totalAmount - discountAmount + shippingFee - discountShippingFee;

                // Tạo đối tượng OrderPreviewDTO để trả về
                OrderPreviewDTO orderPreviewDTO = OrderPreviewDTO.builder()
                                .orderDetails(orderDetails)
                                .totalAmount(totalAmount)
                                .discountAmount(discountAmount)
                                .shippingFee(shippingFee)
                                .discountShippingFee(discountShippingFee)
                                .finalAmount(finalAmount)
                                .appliedPromotions(new ArrayList<>())
                                .build();

                // Áp dụng khuyến mãi tự động không cần nhập mã khuyến mãi
                List<Promotion> automaticPromotions = promotionRepository
                                .findByPromotionTypeAndStartDateBeforeAndEndDateAfter(
                                                PromotionTypeEnum.AUTOMATIC, java.time.LocalDateTime.now(),
                                                java.time.LocalDateTime.now());

                // Kiểm tra điều kiện và áp dụng từng khuyến mãi tự động
                for (Promotion promotion : automaticPromotions) {
                        // Kiểm tra xem khuyến mãi có đang hoạt động hay không
                        if (promotion.getIsActive() == null ||
                                        !promotion.getIsActive() ||
                                        promotion.getStartDate() == null ||
                                        promotion.getEndDate() == null ||
                                        promotion.getStartDate().isAfter(java.time.LocalDateTime.now()) ||
                                        promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
                                throw new NotFoundException("Promotion is not active");
                        }

                        // Kiểm tra điều kiện áp dụng khuyến mãi
                        // Nếu tất cả điều kiện của khuyến mãi đều là PRODUCT_SPECIFIC thì sẽ bỏ qua
                        // khuyến mãi này vì nó đã được áp dụng tự động thông qua quan hệ giữa Product
                        // và Promotion
                        boolean checkCondtion = promotion.getPromotionConditions().stream()
                                        .allMatch((promotionCondition) -> promotionCondition
                                                        .getConditionType() == PromotionConditionTypeEnum.PRODUCT_SPECIFIC);

                        // Nếu tất cả hành động của khuyến mãi đều là PRODUCT_FIXED_DISCOUNT hoặc
                        // PRODUCT_PERCENT_DISCOUNT thì sẽ bỏ qua khuyến mãi này vì nó đã được áp dụng
                        // tự động thông qua quan hệ giữa Product và Promotion
                        // Nhưng nếu tồn tại bất kỳ hành động nào khác thì vẫn phải áp dụng khuyến mãi
                        // bình thường để đảm bảo các hành động đó được thực thi
                        boolean checkAction = promotion.getPromotionActions().stream()
                                        .allMatch((promotionAction) -> promotionAction
                                                        .getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT
                                                        || promotionAction
                                                                        .getActionType() == PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT);

                        // Nếu tất cả điều kiện đều là PRODUCT_SPECIFIC và tất cả hành động đều là
                        // PRODUCT_FIXED_DISCOUNT hoặc PRODUCT_PERCENT_DISCOUNT thì bỏ qua khuyến mãi
                        // này
                        if (checkCondtion && checkAction) {
                                continue;
                        }

                        // Kiểm tra điều kiện áp dụng khuyến mãi
                        List<PromotionCondition> conditions = promotion.getPromotionConditions();

                        // Biến isApplicable để xác định xem khuyến mãi có áp dụng được cho đơn hàng hay
                        // không, ban đầu được đặt là true và sẽ bị đổi thành false nếu có bất kỳ điều
                        // kiện nào không được thỏa mãn
                        boolean isApplicable = true;
                        for (PromotionCondition condition : conditions) {

                                PromotionConditionTypeEnum conditionType = condition.getConditionType();

                                // Lấy factory tương ứng với loại điều kiện để kiểm tra xem điều kiện đó có được
                                PromotionConditionStrategy conditionFactory = promotionConditionFactory
                                                .getPromotionConditionStrategy(conditionType);

                                Map<String, Object> conditionValue = condition.getValue();

                                if (!conditionFactory.isSatisfied(orderPreviewDTO, conditionValue)) {
                                        isApplicable = false;
                                        break;
                                }

                        }

                        // Nếu khuyến mãi áp dụng được cho đơn hàng thì thực thi các hành động của
                        // khuyến mãi đó
                        if (isApplicable) {

                                // Áp dụng các hành động của khuyến mãi lên orderPreviewDTO thông qua factory
                                // tương ứng với từng loại hành động
                                List<PromotionAction> actions = promotion.getPromotionActions();

                                for (PromotionAction action : actions) {

                                        // Nếu checkCondition là true
                                        // Và actionType là PRODUCT_FIXED_DISCOUNT hoặc PRODUCT_PERCENT_DISCOUNT thì sẽ
                                        // bỏ qua hành động này vì nó đã được áp dụng
                                        if (checkCondtion && action
                                                        .getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT
                                                        || action.getActionType() == PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT) {
                                                continue;
                                        }

                                        PromotionActionTypeEnum actionType = action.getActionType();

                                        // Map<String, Object> actionValue = action.getValue();

                                        PromotionActionStrategy actionFactory = promotionActionFactory
                                                        .getPromotionActionStrategy(actionType);

                                        actionFactory.execute(orderPreviewDTO, promotion,
                                                        promotion.getPromotionActions().indexOf(action));

                                }
                                orderPreviewDTO.getAppliedPromotions().add(promotion.getPromotionId());

                        }

                }
                // Áp dụng khuyến mãi khi khách hàng nhập mã khuyến mãi
                List<Integer> promotionIds = createPreviewDTO.getPromotionIds();

                // Áp dụng khuyến mãi do nhập mã khuyến mãi
                if (promotionIds == null || promotionIds.isEmpty()) {
                        return orderPreviewDTO;
                }

                List<Promotion> promotions = promotionRepository.findAllById(promotionIds);

                // Kiểm tra xem tất cả các mã khuyến mãi có tồn tại hay không
                if (promotions.size() != promotionIds.size()) {
                        throw new NotFoundException("Some promotions not found");
                }

                List<VoucherWallet> voucherWallets = voucherWalletRepository
                                .findByPromotion_PromotionIdInAndCustomer_UserId(promotionIds,
                                                customerId);

                // Kiểm tra xem tất cả các mã khuyến mãi có thuộc về khách hàng hay không
                if (voucherWallets.size() != promotionIds.size()) {
                        throw new NotFoundException("Some promotions not found in customer's voucher wallet");
                }

                // Kiểm tra xem các mã khuyến mãi có thể xếp chồng lên nhau hay không, nếu không
                // thì ném lỗi
                if (!hasAllStackable(promotions)) {
                        throw new ConflictException("Some promotions cannot be stacked together");
                }

                Set<Integer> x = productDetails.stream()
                                .map((productDetail) -> productDetail.getProductColor().getProduct().getPromotion()
                                                .getPromotionId())
                                .collect(Collectors.toSet());

                Set<Promotion> promotionProduct = promotionRepository.findAllById(x).stream()
                                .collect(Collectors.toSet());

                promotions.addAll(promotionProduct);

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

                                        // Map<String, Object> actionValue = action.getValue();

                                        PromotionActionStrategy actionFactory = promotionActionFactory
                                                        .getPromotionActionStrategy(actionType);

                                        actionFactory.execute(orderPreviewDTO, promotion,
                                                        promotion.getPromotionActions().indexOf(action));

                                }
                                orderPreviewDTO.getAppliedPromotions().add(promotion.getPromotionId());

                        }

                }

                Set<Integer> promotionProductIds = productDetails.stream()
                                .map((productDetail) -> productDetail.getProductColor().getProduct().getPromotion()
                                                .getPromotionId())
                                .collect(Collectors.toSet());

                orderPreviewDTO.getAppliedPromotions().addAll(promotionProductIds);

                return orderPreviewDTO;
        }

        private boolean hasAllStackable(List<Promotion> promotions) {
                if (promotions.size() <= 1) {
                        return true; // Nếu chỉ có một khuyến mãi hoặc không có khuyến mãi nào, thì mặc định là có
                                     // thể xếp chồng
                }
                return promotions.stream().allMatch(
                                (promotion) -> promotion.getStackable() == true);
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

}
