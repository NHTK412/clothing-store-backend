package com.example.clothingstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.PromotionTargetUser;

@Repository
public interface PromotionTargetUserRepository extends JpaRepository<PromotionTargetUser, Integer> {

}
