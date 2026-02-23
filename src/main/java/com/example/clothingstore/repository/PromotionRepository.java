package com.example.clothingstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.enums.PromotionTypeEnum;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    // Tìm các mã khuyển mãi trong khoảng thời gian hiện tại và có kiểu là AUTOMATIC
    List<Promotion> findByPromotionTypeAndStartDateBeforeAndEndDateAfter(PromotionTypeEnum promotionType,
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // Danh sách các mã khuyến mãi đang hoạt động hoặc không hoạt động
    List<Promotion> findByIsActive(Boolean isActive);

    List<Promotion> findByPromotionTypeAndIsActive(PromotionTypeEnum promotionType, Boolean isActive);

    Optional<Promotion> findByCouponCode(String couponCode);

}