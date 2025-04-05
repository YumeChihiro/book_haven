package com.haven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haven.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Query(value = "SELECT p.product_id AS productId, p.name, sp.price, p.picture, COALESCE(SUM(od.quantity), 0) AS totalSold "
			+ "FROM products p " + "JOIN shop_product sp ON p.product_id = sp.product_id "
			+ "JOIN shop_subscriptions ss ON sp.manager_id = ss.manager_id "
			+ "LEFT JOIN order_details od ON p.product_id = od.product_id "
			+ "LEFT JOIN orders o ON o.order_id = od.order_id "
			+ "LEFT JOIN order_statuses os ON os.order_status_id = o.order_status_id " + "WHERE ss.status = 'active' "
			+ "AND ss.end_date > CURRENT_TIMESTAMP " + "AND (os.order_status_id IS NULL OR os.name = 'Delivered') "
			+ "GROUP BY p.product_id, p.name, sp.price, p.picture", nativeQuery = true)
	List<Object[]> findProductsFromActiveSubscribedShopsNative();

	@Query(value = "SELECT CAST(p.product_id AS SIGNED) AS productId, p.name, p.description, p.picture, p.weight, " +
	           "CAST(pub.publisher_id AS SIGNED) AS publisherId, pub.name AS publisherName, " +
	           "CAST(a.author_id AS SIGNED) AS authorId, a.name AS authorName, " +
	           "CAST(c.category_id AS SIGNED) AS categoryId, c.name AS categoryName, " +
	           "CAST(sp.id AS SIGNED) AS shopProductId, CAST(sp.manager_id AS SIGNED) AS shopProductManagerId, " +
	           "CAST(sp.product_id AS SIGNED) AS shopProductProductId, sp.price, CAST(sp.quantity AS SIGNED) AS quantity, " +
	           "CAST(m.manager_id AS SIGNED) AS managerId, m.manager_name AS managerName, " +
	           "COALESCE(SUM(od.quantity), 0) AS totalSold " +
	           "FROM products p " +
	           "LEFT JOIN publishers pub ON p.publisher_id = pub.publisher_id " +
	           "LEFT JOIN authors a ON p.author_id = a.author_id " +
	           "LEFT JOIN categories c ON p.category_id = c.category_id " +
	           "LEFT JOIN shop_product sp ON p.product_id = sp.product_id " +
	           "LEFT JOIN managers m ON sp.manager_id = m.manager_id " +
	           "LEFT JOIN order_details od ON p.product_id = od.product_id " +
	           "LEFT JOIN orders o ON o.order_id = od.order_id " +
	           "LEFT JOIN order_statuses os ON os.order_status_id = o.order_status_id " +
	           "WHERE p.product_id = :productId " +
	           "AND (os.order_status_id IS NULL OR os.name = 'Delivered') " +
	           "GROUP BY p.product_id, p.name, p.description, p.picture, p.weight, " +
	           "pub.publisher_id, pub.name, " +
	           "a.author_id, a.name, " +
	           "c.category_id, c.name, " +
	           "sp.id, sp.manager_id, sp.product_id, sp.price, sp.quantity, " +
	           "m.manager_id, m.manager_name " +
	           "LIMIT 1",
	           nativeQuery = true)
	    Object[] findProductDetailsWithSalesByIdNative(@Param("productId") Integer productId);
	
	// Truy vấn lấy một shop liên quan nhất
    @Query(value = "SELECT m.manager_id AS managerId, m.manager_name AS managerName, COUNT(sp.product_id) AS productCount "
            + "FROM managers m "
            + "JOIN shop_product sp ON m.manager_id = sp.manager_id "
            + "JOIN products p ON p.product_id = sp.product_id "
            + "JOIN shop_subscriptions ss ON m.manager_id = ss.manager_id "
            + "JOIN authors a ON p.author_id = a.author_id "
            + "JOIN categories c ON p.category_id = c.category_id "
            + "WHERE ss.status = 'active' "
            + "AND ss.end_date > CURRENT_TIMESTAMP "
            + "AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword% "
            + "OR a.name LIKE %:keyword% OR c.name LIKE %:keyword%) "
            + "GROUP BY m.manager_id, m.manager_name "
            + "ORDER BY productCount DESC LIMIT 1", nativeQuery = true)
    Object[] findTopShopByProductKeyword(@Param("keyword") String keyword);
    
 // Truy vấn lấy danh sách sản phẩm với tìm kiếm mở rộng
    @Query(value = "SELECT p.product_id AS productId, p.name, sp.price, p.picture, COALESCE(SUM(od.quantity), 0) AS totalSold "
            + "FROM products p "
            + "JOIN shop_product sp ON p.product_id = sp.product_id "
            + "JOIN shop_subscriptions ss ON sp.manager_id = ss.manager_id "
            + "JOIN authors a ON p.author_id = a.author_id "
            + "JOIN categories c ON p.category_id = c.category_id "
            + "LEFT JOIN order_details od ON p.product_id = od.product_id "
            + "LEFT JOIN orders o ON o.order_id = od.order_id "
            + "LEFT JOIN order_statuses os ON os.order_status_id = o.order_status_id "
            + "WHERE ss.status = 'active' "
            + "AND ss.end_date > CURRENT_TIMESTAMP "
            + "AND (os.order_status_id IS NULL OR os.name = 'Delivered') "
            + "AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword% "
            + "OR a.name LIKE %:keyword% OR c.name LIKE %:keyword%) "
            + "GROUP BY p.product_id, p.name, sp.price, p.picture", nativeQuery = true)
    List<Object[]> findProductsFromActiveSubscribedShopsNative(@Param("keyword") String keyword);
    
 // Truy vấn lấy danh sách sản phẩm theo thể loại
    @Query(value = "SELECT p.product_id AS productId, p.name, sp.price, p.picture, COALESCE(SUM(od.quantity), 0) AS totalSold " +
            "FROM products p " +
            "JOIN shop_product sp ON p.product_id = sp.product_id " +
            "JOIN shop_subscriptions ss ON sp.manager_id = ss.manager_id " +
            "LEFT JOIN order_details od ON p.product_id = od.product_id " +
            "LEFT JOIN orders o ON o.order_id = od.order_id " +
            "LEFT JOIN order_statuses os ON os.order_status_id = o.order_status_id " +
            "WHERE p.category_id = :categoryId " +
            "AND ss.status = 'active' " +
            "AND ss.end_date > CURRENT_TIMESTAMP " +
            "AND (os.order_status_id IS NULL OR os.name = 'Delivered') " +
            "GROUP BY p.product_id, p.name, sp.price, p.picture", nativeQuery = true)
    List<Object[]> findProductsByCategoryId(@Param("categoryId") Integer categoryId);

    // Truy vấn lấy danh sách sản phẩm theo tác giả
    @Query(value = "SELECT p.product_id AS productId, p.name, sp.price, p.picture, COALESCE(SUM(od.quantity), 0) AS totalSold " +
            "FROM products p " +
            "JOIN shop_product sp ON p.product_id = sp.product_id " +
            "JOIN shop_subscriptions ss ON sp.manager_id = ss.manager_id " +
            "LEFT JOIN order_details od ON p.product_id = od.product_id " +
            "LEFT JOIN orders o ON o.order_id = od.order_id " +
            "LEFT JOIN order_statuses os ON os.order_status_id = o.order_status_id " +
            "WHERE p.author_id = :authorId " +
            "AND ss.status = 'active' " +
            "AND ss.end_date > CURRENT_TIMESTAMP " +
            "AND (os.order_status_id IS NULL OR os.name = 'Delivered') " +
            "GROUP BY p.product_id, p.name, sp.price, p.picture", nativeQuery = true)
    List<Object[]> findProductsByAuthorId(@Param("authorId") Integer authorId);
}
