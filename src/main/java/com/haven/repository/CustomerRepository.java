package com.haven.repository;

import com.haven.entity.Customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
    Optional<Customer> findByEmail(String email);
    
    Optional<Customer> findByCustomerId(Integer customerId);
    
    boolean existsByEmail(String email);
}