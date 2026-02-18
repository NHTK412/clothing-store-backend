// package com.example.clothingstore.model;

// import java.util.List;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.JoinTable;
// import jakarta.persistence.ManyToMany;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;

// @Entity
// @Table(name = "PromotionGroup")
// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class PromotionGroup extends Base {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "GroupId")
//     private Integer groupId;

//     @Column(name = "GroupName")
//     private String groupName;

//     @Column(name = "Description")
//     private String description;

//     @Column(name = "MinPurchaseQuantity")
//     private Integer minPurchaseQuantity;

//     @ManyToOne
//     @JoinColumn(name = "PromotionId")
//     private Promotion promotion;

//     @ManyToMany
//     @JoinTable(
//     name = "PromotionGroup_ProductDetail" , 
//     joinColumns = @JoinColumn(name = "GroupId") , 
//     inverseJoinColumns = @JoinColumn(name = "DetailId")
//     )
//     private List<ProductDetail> productDetails;

    

// }
