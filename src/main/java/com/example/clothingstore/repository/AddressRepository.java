package com.example.clothingstore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findByAddressIdAndCustomer_CustomerId(Integer addressId, Integer customerId);

    Page<Address> findByCustomer_CustomerId(Integer customerId, Pageable pageable);
}
