package com.haven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.SubscriptionBenefit;

@Repository
public interface SubscriptionBenefitRepository extends JpaRepository<SubscriptionBenefit, Integer>{
	List<SubscriptionBenefit> findByPlanId(Integer planId);
}
