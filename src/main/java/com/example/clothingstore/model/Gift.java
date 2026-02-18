// package com.example.clothingstore.model;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;

// @Entity
// @Table(name = "Git")
// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class Gift extends Base {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "GiftId")
//     private Integer giftId;

//     @Column(name = "GiftQuantity")
//     private Integer giftQuantity;

//     @Column(name = "MaxGift")
//     private Integer maxGift;

//     @ManyToOne
//     @JoinColumn(name = "PromotionId")
//     private Promotion promotion;

//     @ManyToOne
//     @JoinColumn(name = "DetailId")
//     private ProductDetail productDetail;
// }
