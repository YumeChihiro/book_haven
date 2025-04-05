package com.haven.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.haven.dto.ManagerUpdateDTO;
import com.haven.entity.Manager;
import com.haven.entity.ManagerAccount;
import com.haven.repository.ManagerRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ManagerService {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);
	
	@Autowired
	private ManagerRepository managerRepository;
	
	@Transactional
    public ManagerUpdateDTO updateManager(int managerId, ManagerUpdateDTO updateDTO, UserDetails userDetails) {
        // Tìm Manager
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found"));

        // Kiểm tra quyền
        if (!userDetails.getUsername().equals(manager.getEmail()) && 
            !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Unauthorized");
        }

        // Cập nhật các trường của Manager
        if (updateDTO.getManagerName() != null && !updateDTO.getManagerName().trim().isEmpty()) {
            manager.setManagerName(updateDTO.getManagerName().trim());
        }
        if (updateDTO.getAvatar() != null) {
            manager.setAvatar(updateDTO.getAvatar());
        }

        // Lưu Manager
        managerRepository.save(manager);
        logger.info("Manager updated: id={}, name={}", manager.getManagerId(), manager.getManagerName());
        
        ManagerAccount managerAccount = manager.getManagerAccount();
        if (managerAccount != null) {
      
            managerAccount.setUpdateAt(LocalDateTime.now());
            managerAccount.setRecentActivity(LocalDateTime.now());
            logger.info("ManagerAccount found for customerId={}, updating updateAt={}", 
                managerId, managerAccount.getUpdateAt());
        } else {
            logger.info("No ManagerAccount associated with customerId={}", managerId);
        }

        // Tạo DTO trả về
        ManagerUpdateDTO responseDTO = new ManagerUpdateDTO();
        responseDTO.setManagerName(manager.getManagerName());
        responseDTO.setAvatar(manager.getAvatar());

        return responseDTO;
    }
	
	public Manager findById(int managerId) {
        return managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Manager với ID: " + managerId));
    }

}
