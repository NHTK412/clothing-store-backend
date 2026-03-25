package com.example.clothingstore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    Page<Order> findAllByCustomerId(Integer customerId, Pageable pageable);

    Optional<Order> findByOrderIdAndCustomer_UserId(Integer orderId, Integer customerId);

}