package com.example.clothingstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.VoucherWallet;

@Repository
public interface VoucherWalletRepository extends JpaRepository<VoucherWallet, Integer> {

    // Lấy wallet theo customerId
    List<VoucherWallet> findByCustomer_UserId(Integer customerId);

    // Lấy wallet theo nhiều promotionId và customerId
    List<VoucherWallet> findByPromotion_PromotionIdInAndCustomer_UserId(
            List<Integer> promotionIds,
            Integer customerId);

    Page<VoucherWallet> findByCustomer_UserId(Integer customerId, Pageable pageable);

    Boolean existsByPromotion_PromotionIdAndCustomer_UserId(Integer promotionId, Integer customerId);


}
