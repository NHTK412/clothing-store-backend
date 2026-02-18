package com.example.clothingstore.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
// import org.springframework.jdbc.core.SqlTypeValue;

import com.example.clothingstore.enums.PromotionActionTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
// import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PromotionAction")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromotionAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromotionActionId")
    private Integer promotionActionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ActionType")
    private PromotionActionTypeEnum actionType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "Value", columnDefinition = "json")
    private Map<String, Object> value;

    @ManyToOne
    @JoinColumn(name = "PromotionId")
    private Promotion promotion;
}
