package com.haven.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.haven.entity.ShopSubscription;
import com.haven.entity.SubscriptionStatus;

@Repository
public interface ShopSubscriptionRepository extends JpaRepository<ShopSubscription, Integer> {
    List<ShopSubscription> findByManagerManagerId(Integer managerId);
    
    List<ShopSubscription> findByStatus(SubscriptionStatus status);

    // Tối ưu: Lấy các gói theo trạng thái và endDate trước một ngày nhất định
    @Query("SELECT s FROM ShopSubscription s WHERE s.status = :status AND s.endDate <= :endDate")
    List<ShopSubscription> findByStatusAndEndDateBefore(
        @Param("status") SubscriptionStatus status,
        @Param("endDate") LocalDateTime endDate
    );
    
    List<ShopSubscription> findByPlanPlanNameAndStatusAndEndDateAfter(
            String planName, 
            SubscriptionStatus status, 
            LocalDateTime endDate
        );
}