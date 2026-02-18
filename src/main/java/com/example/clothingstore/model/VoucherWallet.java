package com.example.clothingstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VoucherWallet")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VoucherWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherWalletId")
    private Integer voucherWalletId;

    @ManyToOne
    @JoinColumn(name = "PromotionId")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    // Số lượng đã sử dụng
    // Khi check sẽ check này với promotion để xem đã sử dụng hết số lần được phép sử dụng chưa
    @Column(name = "UsedCount")
    private Integer usedCount;

}
