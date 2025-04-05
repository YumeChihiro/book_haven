package com.haven.repository;

import com.haven.entity.FeedbackReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackReportRepository extends JpaRepository<FeedbackReport, Integer> {
	List<FeedbackReport> findByReportedByCustomerCustomerId(Integer customerId);
    List<FeedbackReport> findByReportedByManagerManagerId(Integer managerId);
}