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

import com.example.clothingstore.enums.PromotionApplicationTypeEnum;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    // @Column(name = "Priority")
    // private Integer priority;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;

    @Column(name = "Stackable")
    private Boolean stackable; // Có cho phép xếp chồng với các khuyến mãi khác hay không  --> chỉ có hiệu lực khi promotionType là CONDITIONAL hoặc COUPON_CODE

    // Vì ở đây có 3 loại khuyến mãi: tự động áp dụng, được phát và phải nhập nên sẽ có 3 cột tương ứng để lưu thông tin
    // Việc sử dụng stackable là để xử lý 2 role như sau:
    // - Case1: Luôn sử lý logic nếu k cho nạp chồng và cho nạp chồng ở các class triển khai interface. Vì khuyến mãi loại autowire sẽ luôn được áp dụng trước rồi mới tới khuyến mãi trong ví. 
    // - Case2: Dùng để xử lý logic khi mà chọn khuyến mãi trong ví. Vì nếu stackable là true thì customer sẽ được chọn nhiều cái để áp dụng ở tầng FE. Còn nếu stackable là false thì customer chỉ được chọn 1 cái để áp dụng ở tầng FE. 

    // @Column(name = "IsCoupon")
    // private Boolean isCoupon; // Có cần nhập mã giảm giá hay không

    @Column(name = "CouponCode")
    private String couponCode; // Nếu isCoupon = true thì sẽ có trường này để nhập mã giảm giá

    @Column(name = "UsageLimit")
    private Integer usageLimit; // Giới hạn số lần sử dụng cho mỗi khách hàng

    @Enumerated(EnumType.STRING)
    @Column(name = "PromotionType")
    private PromotionTypeEnum promotionType; // Tự động áp dụng, được phát và phải nhập

    @Enumerated(EnumType.STRING)
    @Column(name = "ApplicationType")
    private PromotionApplicationTypeEnum applicationType; // Áp dụng cho sản phẩm cụ thể hay áp dụng cho đơn hàng

    // Mã này áp dụng cho mọi khách hàng hay chỉ áp dụng cho một nhóm khách hàng cụ
    // thể
    @Enumerated(EnumType.STRING)
    @Column(name = "PromotionScopeType")
    private PromotionScopeTypeEnum promotionScopeType; // Tất cả khách hàng, nhóm khách hàng cụ thể, hạng thành viên cụ
                                                       // thể

    @OneToMany(mappedBy = "promotion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PromotionCondition> promotionConditions;

    @OneToMany(mappedBy = "promotion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PromotionAction> promotionActions;

    @OneToMany(mappedBy = "promotion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PromotionGroup> promotionGroups;

    // SCOPE
    @OneToMany(mappedBy = "promotion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PromotionTargetUser> promotionTargetUsers;

    @OneToMany(mappedBy = "promotion", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PromotionMemberTier> promotionMemberTiers;
    // SCOPE

    // Trừ applicationType là PRODUCT_LEVEL
    // Scope sẽ được sử dụng khi có kiểu promotionType là CONDITIONAL và COUPON_CODE

    // Trước khi phát hoặc nhập mã lưu vào ví sẽ phải check scope


    @ManyToOne
    @JoinColumn(name = "adminId")
    private Admin admin;
}
