package com.example.clothingstore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.order.OrderRequestDTO;
import com.example.clothingstore.dto.order.OrderResponseDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailResponseDTO;
import com.example.clothingstore.dto.ordergift.OrderGiftResponseDTO;
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.PaymentMethodEnum;
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

        // @Autowired
        // private OrderRepository orderRepository;

        // @Autowired
        // private ProductDetailRepository productDetailRepository;

        // @Autowired
        // private PromotionRepository promotionRepository;

        // @Autowired
        // private CustomerRepository customerRepository;

        // @Autowired
        // private CartRepository cartRepository;

        private final OrderRepository orderRepository;
        private final ProductDetailRepository productDetailRepository;
        private final PromotionRepository promotionRepository;
        private final CustomerRepository customerRepository;
        private final CartRepository cartRepository;

        // @Transactional
        // public OrderResponseDTO createOrder(String userName, OrderRequestDTO
        // orderRequestDTO) {

        // Order order = new Order();

        // order.setDeliveryDate(LocalDateTime.now().plusDays(3));

        // order.setStatus(OrderStatusEnum.PLACED);

        // Customer customer = customerRepository.findByUserName(userName)
        // .orElseThrow(() -> new NotFoundException("Customer not found"));

        // order.setCustomer(customer);

        // ShippingAddress shippingAddress = customer.getShippingAddresses()
        // .stream()
        // .filter(addr ->
        // addr.getAddressId().equals(orderRequestDTO.getAddressShippingId()))
        // .findFirst()
        // .orElseThrow(() -> new NotFoundException("Address shipping not found"));

        // order.setRecipientName(shippingAddress.getRecipientName());

        // order.setPhoneNumber(shippingAddress.getPhoneNumber());

        // order.setDetailedAddress(shippingAddress.getDetailedAdress());

        // order.setWard(shippingAddress.getWard());

        // order.setProvince(shippingAddress.getProvince());

        // Map<Integer, Integer> productDetailMaps =
        // orderRequestDTO.getOrderDetailRequestDTOs()
        // .stream()
        // .collect(Collectors.toMap(odr -> odr.getProductDetailId(), odr ->
        // odr.getQuantity()));

        // List<ProductDetail> productDetails =
        // productDetailRepository.findAllById(productDetailMaps.keySet());

        // List<OrderDetail> orderDetails = productDetails
        // .stream()
        // .map(pd -> {
        // if (pd.getQuantity() < productDetailMaps.get(pd.getDetailId())) {
        // throw new NotFoundException("Product detail with id " + pd.getDetailId()
        // + " is out of stock");
        // }
        // OrderDetail od = new OrderDetail();
        // od.setProductName(pd.getProductColor().getProduct().getProductName());
        // od.setProductImage(pd.getProductColor().getProductImage());
        // od.setColor(pd.getProductColor().getColor());
        // od.setSize(pd.getSize());
        // od.setPrice(pd.getProductColor().getProduct().getUnitPrice()
        // * (1 - pd.getProductColor().getProduct().getDiscount() / 100));
        // od.setQuantity(productDetailMaps.get(pd.getDetailId())); // Giả sử mỗi sản
        // phẩm
        // // đặt 1 cái, có thể mở
        // // rộng sau
        // od.setOrder(order);

        // od.setProductDetail(pd);

        // pd.setQuantity(pd.getQuantity() - productDetailMaps.get(pd.getDetailId()));

        // return od;
        // })
        // .toList();

        // Double totalAmount = orderDetails
        // .stream()
        // .mapToDouble(od -> od.getPrice() * od.getQuantity())
        // .sum();

        // order.setTotalAmount(totalAmount);

        // order.setOrderDetails(orderDetails);

        // order.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        // order.setShippingFee((order.getTotalAmount() >= 1000000.0) ? 0.0 : 30000.0);

        // order.setDiscountAmount(0.0);

        // productDetailRepository.saveAll(productDetails);

        // orderRepository.save(order);

        // OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        // orderResponseDTO.setOrderId(order.getOrderId());

        // orderResponseDTO.setTotalAmount(order.getTotalAmount());

        // orderResponseDTO.setDiscountAmount(order.getDiscountAmount());

        // orderResponseDTO.setShippingFee(order.getShippingFee());

        // orderResponseDTO.setDeliveryDate(order.getDeliveryDate());

        // orderResponseDTO.setStatus(order.getStatus());

        // orderResponseDTO.setRecipientName(order.getRecipientName());

        // orderResponseDTO.setPhoneNumber(order.getPhoneNumber());

        // orderResponseDTO.setDetailedAddress(order.getDetailedAddress());

        // orderResponseDTO.setWard(order.getWard());

        // orderResponseDTO.setProvince(order.getProvince());

        // orderResponseDTO.setZaloAppTransId(order.getZaloAppTransId());

        // // Map order detail
        // List<OrderDetailResponseDTO> orderDetailResponseDTOs =
        // order.getOrderDetails()
        // .stream()
        // .map(od -> {
        // OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();

        // orderDetailResponseDTO.setProductName(od.getProductName());

        // orderDetailResponseDTO.setProductImage(od.getProductImage());

        // orderDetailResponseDTO.setColor(od.getColor());

        // orderDetailResponseDTO.setSize(od.getSize());

        // orderDetailResponseDTO.setQuantity(od.getQuantity());

        // orderDetailResponseDTO.setPrice(od.getPrice());

        // return orderDetailResponseDTO;
        // })
        // .toList();

        // orderResponseDTO.setOrderDetails(orderDetailResponseDTOs);

        // // Map order gift
        // if (order.getOrderGifts() != null) {
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

        // }

        // // Điều chỉnh cart
        // Cart cart =
        // cartRepository.findByCustomer_CustomerId(order.getCustomer().getCustomerId())
        // .orElseThrow(() -> new NotFoundException("Cart not found"));

        // cart.getCartItems().removeIf(
        // cartItem ->
        // productDetailMaps.containsKey(cartItem.getProductDetail().getDetailId()));

        // cartRepository.save(cart);

        // return orderResponseDTO;
        // }

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

                // 5. Chuẩn bị dữ liệu sản phẩm
                Map<Integer, Integer> productDetailMaps = orderRequestDTO.getOrderDetailRequestDTOs()
                                .stream()
                                .collect(Collectors.toMap(odr -> odr.getProductDetailId(), odr -> odr.getQuantity()));

                List<ProductDetail> productDetails = productDetailRepository.findAllById(productDetailMaps.keySet());

                // 6. Tạo orderDetails và trừ tồn kho
                List<OrderDetail> orderDetails = productDetails.stream().map(pd -> {
                        if (pd.getQuantity() < productDetailMaps.get(pd.getDetailId())) {
                                throw new NotFoundException(
                                                "Product detail with id " + pd.getDetailId() + " is out of stock");
                        }
                        OrderDetail od = new OrderDetail();
                        od.setProductName(pd.getProductColor().getProduct().getProductName());
                        od.setProductImage(pd.getProductColor().getProductImage());
                        od.setColor(pd.getProductColor().getColor());
                        od.setSize(pd.getSize());
                        od.setPrice(pd.getProductColor().getProduct().getUnitPrice()
                                        * (1 - pd.getProductColor().getProduct().getDiscount() / 100));
                        od.setQuantity(productDetailMaps.get(pd.getDetailId()));
                        od.setOrder(order);
                        od.setProductDetail(pd);

                        // Trừ số lượng tồn kho
                        pd.setQuantity(pd.getQuantity() - productDetailMaps.get(pd.getDetailId()));

                        return od;
                }).toList();

                // 7. Tính tổng tiền và set các thông tin khác
                Double totalAmount = orderDetails.stream()
                                .mapToDouble(od -> od.getPrice() * od.getQuantity())
                                .sum();

                order.setTotalAmount(totalAmount);
                order.setOrderDetails(orderDetails);
                order.setPaymentMethod(orderRequestDTO.getPaymentMethod());
                order.setShippingFee((order.getTotalAmount() >= 1000000.0) ? 0.0 : 30000.0);
                order.setDiscountAmount(0.0);

                // 8. Lưu dữ liệu
                productDetailRepository.saveAll(productDetails);
                orderRepository.save(order);

                // 9. Tạo response DTO
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
                orderResponseDTO.setZaloAppTransId(order.getZaloAppTransId());

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
                        return orderDetailResponseDTO;
                }).toList();
                orderResponseDTO.setOrderDetails(orderDetailResponseDTOs);

                // 11. Map order gift
                if (order.getOrderGifts() != null) {
                        List<OrderGiftResponseDTO> orderGiftDTOs = order.getOrderGifts().stream().map(og -> {
                                OrderGiftResponseDTO ogDTO = new OrderGiftResponseDTO();
                                ogDTO.setGiftName(og.getGiftName());
                                ogDTO.setGiftQuantity(og.getGiftQuantity());
                                ogDTO.setGiftImage(og.getGiftImage());
                                ogDTO.setPromotionName(og.getPromotionName());
                                return ogDTO;
                        }).toList();
                        orderResponseDTO.setOrderGifts(orderGiftDTOs);
                }

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
                List<OrderGiftResponseDTO> orderGiftDTOs = order.getOrderGifts()
                                .stream()
                                .map(og -> {
                                        OrderGiftResponseDTO ogDTO = new OrderGiftResponseDTO();

                                        ogDTO.setGiftName(og.getGiftName());

                                        ogDTO.setGiftQuantity(og.getGiftQuantity());

                                        ogDTO.setGiftImage(og.getGiftImage());

                                        ogDTO.setPromotionName(og.getPromotionName());

                                        return ogDTO;
                                })
                                .toList();
                orderResponseDTO.setOrderGifts(orderGiftDTOs);

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
