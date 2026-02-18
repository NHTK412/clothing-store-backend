package com.example.clothingstore.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;

import com.example.clothingstore.enums.PromotionConditionTypeEnum;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Table này dùng để lưu trữ các điều kiện áp dụng của chương trình khuyến mãi, có thể có nhiều điều kiện cho một chương trình khuyến mãi

@Entity
@Table(name = "PromotionCondition")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromotionCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromotionConditionId")
    private Integer promotionConditionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ConditionType")
    private PromotionConditionTypeEnum conditionType;

    @Column(name = "Operator")
    private String operator;

    @JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "Value", columnDefinition = "json")
    private Map<String, Object> value;

    // Chưa gắn quan hệ
    @ManyToOne
    @JoinColumn(name = "PromotionId")
    private Promotion promotion;

}
