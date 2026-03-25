package com.example.clothingstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.PromotionMemberTier;

@Repository
public interface PromotionMemberTierRepository  extends JpaRepository<PromotionMemberTier, Integer> {

}
