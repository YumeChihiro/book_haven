package com.haven.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haven.entity.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer>{
	
	Optional<Manager> findByEmail(String email);
	
	Optional<Manager> findByManagerId(Integer id);
	
	 boolean existsByEmail(String email);
	 
	 @Query(value = "SELECT COUNT(*) " +
	            "FROM shop_product sp " +
	            "WHERE sp.manager_id = :managerId AND sp.quantity > 0", nativeQuery = true)
	    Long countAvailableProducts(@Param("managerId") Integer managerId);

	    // Lấy tổng số lượng sản phẩm đã bán
	    @Query(value = "SELECT COALESCE(SUM(od.quantity), 0) " +
	            "FROM shop_product sp " +
	            "LEFT JOIN order_details od ON sp.product_id = od.product_id " +
	            "LEFT JOIN orders o ON od.order_id = o.order_id " +
	            "LEFT JOIN order_statuses os ON o.order_status_id = os.order_status_id " +
	            "WHERE sp.manager_id = :managerId " +
	            "AND (os.order_status_id IS NULL OR os.name = 'Delivered')", nativeQuery = true)
	    Long sumTotalSoldProducts(@Param("managerId") Integer managerId);

	    // Tính điểm đánh giá trung bình
	    @Query(value = "SELECT AVG(rating) " +
	            "FROM manager_feedbacks " +
	            "WHERE manager_id = :managerId", nativeQuery = true)
	    Double getAverageRating(@Param("managerId") Integer managerId);

	    // Lấy danh sách sản phẩm của shop
	    @Query(value = "SELECT p.product_id, p.name, sp.price, p.picture, COALESCE(SUM(od.quantity), 0) AS total_sold " +
	            "FROM products p " +
	            "JOIN shop_product sp ON p.product_id = sp.product_id " +
	            "LEFT JOIN order_details od ON p.product_id = od.product_id " +
	            "LEFT JOIN orders o ON od.order_id = o.order_id " +
	            "LEFT JOIN order_statuses os ON o.order_status_id = os.order_status_id " +
	            "WHERE sp.manager_id = :managerId " +
	            "AND (os.order_status_id IS NULL OR os.name = 'Delivered') " +
	            "GROUP BY p.product_id, p.name, sp.price, p.picture", nativeQuery = true)
	    List<Object[]> findProductsByManagerId(@Param("managerId") Integer managerId);
	 
}
