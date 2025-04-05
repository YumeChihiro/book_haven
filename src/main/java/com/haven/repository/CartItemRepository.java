package com.haven.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.haven.entity.Cart;
import com.haven.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // Lấy cart items của customer chứa product của shop
    @Query("SELECT ci FROM CartItem ci " +
           "JOIN ci.cart c " +
           "JOIN ci.product p " +
           "JOIN Shop_Product sp ON sp.product = p " +
           "WHERE c.customer.customerId = :customerId AND sp.manager.managerId = :shopId")
    List<CartItem> findByCustomerIdAndShopId(Integer customerId, Integer shopId);
    
    Optional<CartItem> findByCartAndProductProductId(Cart cart, Integer productId);
    
    
}
