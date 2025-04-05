package com.haven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.LoginHistory;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
	
	List<LoginHistory> findByCustomerCustomerId(Integer customerId);

    List<LoginHistory> findByManagerManagerId(Integer managerId);
}
