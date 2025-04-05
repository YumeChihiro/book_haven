package com.haven.repository;

import com.haven.entity.ManagerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerFeedbackRepository extends JpaRepository<ManagerFeedback, Integer> {
    List<ManagerFeedback> findByManagerManagerId(int managerId);
    
    List<ManagerFeedback> findByCustomerCustomerId(int customerId);
}