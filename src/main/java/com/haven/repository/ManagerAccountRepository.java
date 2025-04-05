package com.haven.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.entity.ManagerAccount;

@Repository
public interface ManagerAccountRepository extends JpaRepository<ManagerAccount, Integer>{
	Optional<ManagerAccount> findByCustomer(Customer customer);
	
	Optional<ManagerAccount> findByManager(Manager manager);
	
	 List<ManagerAccount> findByRecentActivity(LocalDateTime dateTime);
}
