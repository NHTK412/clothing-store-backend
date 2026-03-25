package com.example.clothingstore.model;

import com.example.clothingstore.enums.RefundMethodEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RefundPayments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RefundPayment extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RefundPaymentId")
    private Integer refundPaymentId;

    @Column(name = "GatewayRefundId")
    private String gatewayRefundId;

    @Column(name = "ImageRefund")   
    private String imageRefund;

    @Column(name = "Note", columnDefinition = "TEXT")
    private String note;

    

}
