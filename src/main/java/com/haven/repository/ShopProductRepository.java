package com.haven.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haven.entity.Shop_Product;

@Repository
public interface ShopProductRepository extends JpaRepository<Shop_Product, Integer> {
    List<Shop_Product> findByManagerManagerId(int managerId);
    
    boolean existsByManagerManagerIdAndProductProductId(Integer managerId, Integer productId);
    
    @Query(value = "SELECT price FROM shop_product WHERE product_id = :productId LIMIT 1", nativeQuery = true)
    BigDecimal findPriceByProductId(@Param("productId") Integer productId);
    
    @Query(value = "SELECT quantity FROM shop_product WHERE product_id = :productId LIMIT 1", nativeQuery = true)
    Integer findStockByProductId(@Param("productId") Integer productId);
    
    @Query("SELECT sp FROM Shop_Product sp WHERE sp.product.productId = :productId")
    Shop_Product findByProductProductId(Integer productId);

}