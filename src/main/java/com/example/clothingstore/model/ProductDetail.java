package com.example.clothingstore.model;

import java.util.List;

import com.example.clothingstore.enums.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ProductDetail")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetail extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DetailId")
    private Integer detailId;

    // @Column(name = "Color")
    // private String color;

    @Column(name = "Size")
    private String size;

    @Column(name = "Quantity")
    private Integer quantity;

    // @Column(name = "ProductImage")
    // private String productImage;

    // @ManyToOne
    // @JoinColumn(name = "ProductId")
    // private Product product;

    @ManyToOne
    @JoinColumn(name = "ProductColor")
    private ProductColor productColor;

    // @ManyToMany(mappedBy = "productDetails")
    // private List<PromotionGroup> promotionGroups;

    // @OneToMany(mappedBy = "productDetail")
    // private List<Gift> gits;

    // @OneToMany(mappedBy = "productDetail")
    // private List<OrderDetail> orderDetails;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "Status", columnDefinition = "varchar(255) default 'ACTIVE'")
    private StatusEnum status;

}
