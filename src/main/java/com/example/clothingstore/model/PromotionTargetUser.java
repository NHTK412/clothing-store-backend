package com.example.clothingstore.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Table này dùng để lưu thông tin về những khách hàng được áp dụng mã khuyến mãi (nếu mã khuyến mãi đó chỉ áp dụng cho một nhóm khách hàng cụ thể chứ không phải tất cả khách hàng)
@Entity
@Table(name = "PromotionTargetUser")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromotionTargetUser {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromotionTargetUserId")
    private Integer promotionTargetUserId;

    @ManyToOne
    @JoinColumn(name = "PromotionId")
    private Promotion promotion;


    @ManyToOne
    @JoinColumn(name = "CustomerId")
    private Customer customer;
}
