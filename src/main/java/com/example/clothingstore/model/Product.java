package com.example.clothingstore.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.tool.schema.internal.exec.GenerationTarget;

import com.example.clothingstore.enums.StatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Product")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductId")
    private Integer productId;

    @Column(name = "ProductName")
    private String productName;

    @Column(name = "UnitPrice")
    private Double unitPrice;

    @Column(name = "Discount")
    private Double discount;

    @Column(name = "Description")
    private String description;

    @Column(name = "ProductImage")
    private String productImage;

    // @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<ProductDetail> productDetails;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductColor> productColors = new ArrayList<>();

    @ManyToMany(mappedBy = "products")
    private List<Category> categories;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "Status", columnDefinition = "varchar(255) default 'ACTIVE'")
    private StatusEnum status;

    @OneToOne
    @JoinColumn(name = "PromotionId")
    private Promotion promotion; // Thêm quan hệ với Promotion

}
