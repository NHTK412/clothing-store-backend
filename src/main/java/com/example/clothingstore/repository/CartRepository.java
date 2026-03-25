package com.example.clothingstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            LEFT JOIN FETCH c.cartItems ci
            LEFT JOIN FETCH ci.productDetail pd
            LEFT JOIN FETCH pd.productColor pc
            LEFT JOIN FETCH pc.product p
            LEFT JOIN FETCH c.customer cu
            WHERE cu.userId = :customerId
                """)
    Optional<Cart> findByCustomerIdWithALLFetch(Integer customerId);

    Optional<Cart> findByCustomer_UserId(Integer userId);
}