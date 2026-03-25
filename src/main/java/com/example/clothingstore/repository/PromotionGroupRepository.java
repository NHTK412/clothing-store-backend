package com.example.clothingstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.PromotionGroup;

@Repository
public interface PromotionGroupRepository extends JpaRepository<PromotionGroup, Integer> {

    List<PromotionGroup> findByPromotionIsNull();

}
