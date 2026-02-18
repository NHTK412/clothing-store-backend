// package com.example.clothingstore.model;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;

// @Entity
// @Table(name = "Discount")
// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class Discount extends Base {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "DiscountId")
//     private Integer discountId;

//     @Column(name = "DiscountPercentage")
//     private Double discountPercentage;

//     @Column(name = "DiscountAmount")
//     private Double discountAmount;

//     @Column(name = "MaxDiscount")
//     private Double maxDiscount;

//     @OneToOne
//     @JoinColumn(name = "PromotionId")
//     private Promotion promotion;

// }
