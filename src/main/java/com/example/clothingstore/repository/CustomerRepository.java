package com.example.clothingstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.MembershipTier;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByUserName(String userName);

    Boolean existsByUserName(String userName);

    List<Customer> findByMembershipTierIn(List<MembershipTier> membershipTiers);

}
