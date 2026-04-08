package com.example.clothingstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.MembershipTier;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Integer> {

    Optional<MembershipTier> findByTierName(String tierName);

    boolean existsByTierName(String tierName);

    MembershipTier findByCustomers_UserId(Integer customerId);

}
