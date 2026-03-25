package com.example.clothingstore.model;

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

// Table này dùng để lưu thông tin về hạng thành viên của khách hàng, nếu mã khuyến mãi đó chỉ áp dụng cho khách hàng có hạng thành viên cụ thể
@Entity
@Table(name = "PromotionMemberTier")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromotionMemberTier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionMemberTierId;

    @ManyToOne
    @JoinColumn(name = "PromotionId")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "MembershipTierId")
    private MembershipTier membershipTier;

}
