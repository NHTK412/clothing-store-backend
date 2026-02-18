// package com.example.clothingstore.model;

// import java.time.LocalDateTime;
// import java.util.List;

// import com.example.clothingstore.enums.PromotionTypeEnum;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;

// @Entity
// @Table(name = "Promotion")
// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class Promotion extends Base {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "PromotionId")
//     private Integer promotionId;

//     @Column(name = "PromotionName")
//     private String promotionName;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "PromotionType")
//     private PromotionTypeEnum promotionType;

//     @Column(name = "Description")
//     private String description;

//     // @Column(name = "ApplyCondition")
//     // private String applyCondition;

//     @Column(name = "StartDate")
//     private LocalDateTime startDate;

//     @Column(name = "EndDate")
//     private LocalDateTime endDate;

//     // @Column(name = "ApplyType")
//     // private String applyType;

//     @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
//     private List<PromotionGroup> promotionGroups;

//     @OneToOne(mappedBy = "promotion", cascade = CascadeType.ALL)
//     private Discount discount;

//     @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
//     private List<Gift> gits;

// }

package com.example.clothingstore.model;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;

// import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Promotion_New")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Promotion extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromotionId")
    private Integer promotionId;

    @Column(name = "PromotionName")
    private String promotionName;

    @Column(name = "Description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "PromotionType")
    private PromotionTypeEnum promotionType;

    @Column(name = "Priority")
    private Integer priority;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;

    @Column(name = "Stackable")
    private Boolean stackable;

    // @Column(name = "IsCoupon")
    // private Boolean isCoupon; // Có cần nhập mã giảm giá hay không

    @Column(name = "CouponCode")
    private String couponCode; // Nếu isCoupon = true thì sẽ có trường này để nhập mã giảm giá

    @Column(name = "UsageLimit")
    private Integer usageLimit; // Giới hạn số lần sử dụng cho mỗi khách hàng

    @Enumerated(EnumType.STRING)
    @Column(name = "PromotionScopeType")
    private PromotionScopeTypeEnum promotionScopeType; // Mã này áp dụng cho mọi khách hàng hay chỉ áp dụng cho một nhóm
                                                       // khách hàng cụ thể

    @OneToMany(mappedBy = "promotion")
    private java.util.List<PromotionCondition> promotionConditions;

    @OneToMany(mappedBy = "promotion")
    private java.util.List<PromotionAction> promotionActions;
}
