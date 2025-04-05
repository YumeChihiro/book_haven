package com.haven.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.haven.entity.Shipper;
import com.haven.entity.ShippingFee;

@Repository
public interface ShippingFeeRepository extends JpaRepository<Shipper, Integer>{
	
	@Query("SELECT sf FROM ShippingFee sf WHERE sf.weight >= :weight AND sf.distance >= :distance")
    Optional<ShippingFee> findByWeightAndDistance(Float weight, Float distance);
}	
