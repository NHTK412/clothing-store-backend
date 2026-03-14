package com.example.clothingstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OrderDetail")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetail extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DetailId")
    private Integer detailId;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "Price")
    private Double price;

    @Column(name = "Discount")
    private Double discount;

    @Column(name = "FinalPrice")
    private Double finalPrice;

    // Lưu thông tin sản phẩm tại thời điểm mua (de-normalize)
    @Column(name = "ProductName")
    private String productName;

    @Column(name = "ProductImage")
    private String productImage;

    @Column(name = "Color")
    private String color;

    @Column(name = "Size")
    private String size;

    // Reference đến ProductDetail (dùng cho tham khảo, không dùng cập nhật giá)
    @ManyToOne
    @JoinColumn(name = "ProductDetailId")
    private ProductDetail productDetail;

    @ManyToOne(cascade = jakarta.persistence.CascadeType.MERGE)
    @JoinColumn(name = "OrderId")
    private Order order;

    @Column(name = "IsReview")
    private Boolean isReview;

}
