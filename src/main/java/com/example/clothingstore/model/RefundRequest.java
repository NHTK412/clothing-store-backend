package com.example.clothingstore.model;

import java.math.BigDecimal;
import java.util.List;

import com.example.clothingstore.enums.RefundMethodEnum;
import com.example.clothingstore.enums.RefundRequestStatusEnum;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RefundRequests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefundRequest extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RefundRequestId")
    private Long refundRequestId;

    @ManyToOne
    @JoinColumn(name = "OrderId", nullable = false)
    private Order order;

    @Column(name = "Reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @OneToMany(mappedBy = "refundRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefundItem> refundItems;

    @Column(name = "RefundShippingFee", precision = 10, scale = 2)
    private BigDecimal refundShippingFee;

    // @OneToOne(mappedBy = "refundRequest", cascade = CascadeType.ALL,
    // orphanRemoval = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RefundPaymentId")
    private RefundPayment refundPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private RefundRequestStatusEnum status;

    @Enumerated(EnumType.STRING)
    private RefundMethodEnum refundMethod;

}
