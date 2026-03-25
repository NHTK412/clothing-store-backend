package com.example.clothingstore.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RefundItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefundItem extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RefundItemId")
    private Integer refundItemId;

    @ManyToOne
    @JoinColumn(name = "ProductDetailId", nullable = false)
    private ProductDetail productDetail;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "RefundAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @ManyToOne
    @JoinColumn(name = "RefundRequestId", nullable = false)
    private RefundRequest refundRequest;

}
