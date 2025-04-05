package com.haven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByManagerManagerIdAndOrderStatusName(Integer managerId, String statusName);
    
    List<Order> findByCustomerCustomerIdAndOrderStatusOrderstatusIdIn(Integer customerId, List<Integer> orderStatusIds);
    
    List<Order> findByManagerManagerId(Integer managerId);
    
    List<Order> findByOrderStatusOrderstatusId(Integer orderStatusId);
}