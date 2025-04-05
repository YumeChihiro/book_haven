package com.haven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.haven.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{
	
	List<Address> findByManagerManagerId(Integer managerId);
    List<Address> findByCustomerCustomerId(Integer customerId);
}
