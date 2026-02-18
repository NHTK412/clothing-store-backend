package com.example.clothingstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.VoucherWallet;

@Repository
public interface VoucherWalletRepository extends JpaRepository<VoucherWallet, Integer> {

    // Lấy wallet theo customerId
    List<VoucherWallet> findByCustomer_CustomerId(Integer customerId);

    // Lấy wallet theo nhiều promotionId và customerId
    List<VoucherWallet> findByPromotion_PromotionIdInAndCustomer_CustomerId(
            List<Integer> promotionIds,
            Integer customerId);
}
