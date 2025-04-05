package com.haven.repository;

import com.haven.entity.ProductFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductFeedbackRepository extends JpaRepository<ProductFeedback, Integer> {
    List<ProductFeedback> findByProductProductId(int productId);
}