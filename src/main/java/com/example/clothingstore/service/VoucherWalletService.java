package com.example.clothingstore.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
import com.example.clothingstore.dto.promotion.PromotionSummaryDTO;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.customer.ConflictException;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.model.VoucherWallet;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.PromotionRepository;
import com.example.clothingstore.repository.VoucherWalletRepository;
import com.example.clothingstore.strategy.scope.PromotionScopeStrategy;
import com.example.clothingstore.strategy.scope.PromotionScopeFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherWalletService {

    private final VoucherWalletRepository voucherWalletRepository;

    private final PromotionRepository promotionRepository;

    private final CustomerRepository customerRepository;

    private final PromotionScopeFactory promotionScopeFactory;

    // Xem các voucher trong ví của khách hàng

    public List<PromotionSummaryDTO> getVouchersForCustomer(Integer customerId, Pageable pageable) {

        Page<VoucherWallet> voucherWalletPage = voucherWalletRepository.findByCustomer_CustomerId(customerId, pageable);

        List<PromotionSummaryDTO> promotionSummaryDTOs = voucherWalletPage.stream()
                .map((voucherWallet) -> {

                    Promotion promotion = voucherWallet.getPromotion();
                    return PromotionSummaryDTO.builder()
                            .promotionId(promotion.getPromotionId())
                            .promotionName(promotion.getPromotionName())
                            .description(promotion.getDescription())
                            .startDate(promotion.getStartDate())
                            .endDate(promotion.getEndDate())
                            .stackable(promotion.getStackable())
                            .usageLimit(promotion.getUsageLimit())
                            .build();

                })
                .toList();

        return promotionSummaryDTOs;

    }

    // Thêm voucher vào ví của khách hàng
    @Transactional
    public PromotionSummaryDTO addPromotionToWallet(
            Integer customerId,
            String couponCode) {

        Promotion promotion = promotionRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new NotFoundException("Voucher not found"));

        if (voucherWalletRepository.existsByPromotion_PromotionIdAndCustomer_CustomerId(promotion.getPromotionId(),
                customerId)) {
            throw new ConflictException("Voucher already exists in wallet");
        }

        if (promotion.getUsageLimit() <= 0) {
            throw new ConflictException("Voucher usage limit reached");
        }

        if (promotion.getIsActive() == false) {
            throw new ConflictException("Voucher is not active");
        }

        // if (promotion.getStartDate().isAfter(java.time.LocalDateTime.now())
        // || promotion.getEndDate().isBefore(java.time.LocalDateTime.now())) {
        // throw new ConflictException("Voucher is not active");
        // }

        if (promotion.getPromotionType() != PromotionTypeEnum.COUPON_CODE) {
            throw new ConflictException("Promotion is not a coupon code");
        }

        PromotionScopeStrategy promotionScopeStrategy = promotionScopeFactory
                .getPromotionScopeStrategy(promotion.getPromotionScopeType());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Kiểm tra xem khách hàng có thỏa mãn điều kiện của mã khuyến mãi hay không
        if (!promotionScopeStrategy.isScopeSatisfied(customer, promotion)) {
            throw new ConflictException("Customer does not satisfy promotion scope");
        }

        VoucherWallet voucherWallet = new VoucherWallet();
        voucherWallet.setPromotion(promotion);
        voucherWallet.setCustomer(customer);

        VoucherWallet savedVoucherWallet = voucherWalletRepository.save(voucherWallet);

        promotion.setUsageLimit(promotion.getUsageLimit() - 1);
        promotionRepository.save(promotion);

        return PromotionSummaryDTO.builder()
                .promotionId(savedVoucherWallet.getPromotion().getPromotionId())
                .promotionName(savedVoucherWallet.getPromotion().getPromotionName())
                .description(savedVoucherWallet.getPromotion().getDescription())
                .startDate(savedVoucherWallet.getPromotion().getStartDate())
                .endDate(savedVoucherWallet.getPromotion().getEndDate())
                .stackable(savedVoucherWallet.getPromotion().getStackable())
                .usageLimit(savedVoucherWallet.getPromotion().getUsageLimit())
                .build();

    }

}
