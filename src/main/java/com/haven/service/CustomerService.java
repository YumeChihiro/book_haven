package com.haven.service;


import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.haven.dto.CustomerUpdateDTO;
import com.haven.entity.Customer;
import com.haven.entity.ManagerAccount;
import com.haven.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CustomerService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Transactional
    public CustomerUpdateDTO updateCustomer(int customerId, CustomerUpdateDTO updateDTO, UserDetails userDetails) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // Kiểm tra quyền
        if (!userDetails.getUsername().equals(customer.getEmail()) && 
            !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Unauthorized");
        }

        // Cập nhật chỉ khi dữ liệu mới không null hoặc không rỗng, giữ nguyên dữ liệu cũ nếu không
        if (updateDTO.getName() != null && !updateDTO.getName().trim().isEmpty()) {
            customer.setName(updateDTO.getName().trim());
        }
        if (updateDTO.getFullName() != null && !updateDTO.getFullName().trim().isEmpty()) {
            customer.setFullName(updateDTO.getFullName().trim());
        }
       
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().trim().isEmpty()) {
            customer.setPhone(updateDTO.getPhone().trim());
        }
        if (updateDTO.getBirth() != null) {
            customer.setBirth(updateDTO.getBirth());
        }
        if (updateDTO.getGender() != null) {
            customer.setGender(updateDTO.getGender());
        }
        if (updateDTO.getAvatar() != null) {
            customer.setAvatar(updateDTO.getAvatar());
        }
        
        try {
        	customerRepository.save(customer);
		} catch (Exception e) {
			logger.warn("Error save user" + e);
		}
        
        // Xử lý ManagerAccount
        ManagerAccount managerAccount = customer.getManagerAccount();
        if (managerAccount != null) {
      
            managerAccount.setUpdateAt(LocalDateTime.now());
            managerAccount.setRecentActivity(LocalDateTime.now());
            logger.info("ManagerAccount found for customerId={}, updating updateAt={}", 
                customerId, managerAccount.getUpdateAt());
        } else {
            logger.info("No ManagerAccount associated with customerId={}", customerId);
        }
        
        CustomerUpdateDTO responseDTO = new CustomerUpdateDTO();
        responseDTO.setName(customer.getName());
        responseDTO.setFullName(customer.getFullName());
        responseDTO.setPhone(customer.getPhone());
        responseDTO.setBirth(customer.getBirth());
        responseDTO.setGender(customer.getGender());
        responseDTO.setAvatar(customer.getAvatar());

        return responseDTO;
    }
	
	public Customer findByEmail(String email) {
	    return customerRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với email: " + email));
	}
	
	public Customer findById(Integer customerId) {
	    return customerRepository.findByCustomerId(customerId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với id: " + customerId));
	}

}
