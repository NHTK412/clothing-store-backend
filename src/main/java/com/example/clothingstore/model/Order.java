package com.example.clothingstore.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;

import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.PaymentMethodEnum;
// import com.example.clothingstore.enums.PaymentMethodStatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderId")
    private Integer orderId;

    @Column(name = "TotalAmount")
    private Double totalAmount;

    @Column(name = "DiscountAmount")
    private Double discountAmount;

    @Column(name = "ShippingFee")
    private Double shippingFee;

    @Column(name = "DiscountShippingFee")
    private Double discountShippingFee;

    @Column(name = "FinalAmount")
    private Double finalAmount;

    @Column(name = "DeliveryDate")
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private OrderStatusEnum status;

    @Column(name = "RecipientName")
    private String recipientName;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "DetailedAddress")
    private String detailedAddress;

    @Column(name = "Ward")
    private String ward;

    @Column(name = "Province")
    private String province;

    // @Enumerated(EnumType.STRING)
    // @Column(name = "PaymentStatus")
    // private OrderPaymentStatusEnum paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentMethod")
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "PaymentStatus")
    private OrderPaymentStatusEnum paymentStatus;

    @Column(name = "PaymentId")
    private String paymentId;

    // @JdbcTypeCode(SqlTypes.JSON)
    // @Column(name = "PaymentData", columnDefinition = "JSON")
    // private Map<String, Object> paymentData;

    // @Column(name = "ZaloAppTransId")
    // private String zaloAppTransId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    // private List<OrderGift> orderGifts;

    @ManyToOne
    @JoinColumn(name = "CustomerId")
    private Customer customer;

    @Column(name = "IsReview")
    private Boolean isReview;

}
