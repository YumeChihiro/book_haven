package com.haven.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.haven.entity.Address;
import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.entity.ManagerAccount;
import com.haven.entity.Order;
import com.haven.entity.Role;
import com.haven.entity.Role.RoleType;
import com.haven.exception.ResourceNotFoundException;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerAccountRepository;
import com.haven.repository.ManagerRepository;
import com.haven.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class UpgradeCustomerService {
	
	private static final Logger logger = LoggerFactory.getLogger(UpgradeCustomerService.class);

    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final ManagerAccountRepository managerAccountRepository;
    private final RoleRepository roleRepository;

    public UpgradeCustomerService(
            CustomerRepository customerRepository,
            ManagerRepository managerRepository,
            ManagerAccountRepository managerAccountRepository,
            RoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.managerRepository = managerRepository;
        this.managerAccountRepository = managerAccountRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void upgradeCustomerToManager(int customerId, Authentication authentication) {
        // 1. Kiểm tra authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Yêu cầu đăng nhập để thực hiện thao tác này");
        }

        // 2. Tìm customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer không tồn tại"));

        // 3. Tìm role SHOP
        Role shopRole = roleRepository.findByRoleName(RoleType.SHOP)
                .orElseThrow(() -> new ResourceNotFoundException("Role SHOP không tồn tại"));

        // 4. Tạo mới Manager từ thông tin Customer với role SHOP
        Manager manager = new Manager();
        manager.setManagerName(customer.getFullName() != null ? customer.getFullName() : customer.getName());
        manager.setEmail(customer.getEmail());
        manager.setPassword(customer.getPassword());
        manager.setAvatar(customer.getAvatar());
        manager.setRole(shopRole);

        // 5. Xử lý Addresses
        if (customer.getAddresses() != null) {
            List<Address> addresses = new ArrayList<>(customer.getAddresses());
            manager.setAddresses(addresses);
            addresses.forEach(address -> {
                address.setCustomer(null);
                address.setManager(manager);
            });
        }

        // 6. Xử lý Orders
        if (customer.getOrders() != null) {
            List<Order> orders = new ArrayList<>(customer.getOrders());
            orders.forEach(order -> {
                order.setCustomer(null);
                order.setManager(manager);
            });
            manager.setOrders(orders);
        }

        // 7. Lưu Manager trước
        Manager savedManager = managerRepository.save(manager);
        logger.info("Manager created from customerId={}: managerId={}", customerId, savedManager.getManagerId());

     // 8. Xử lý ManagerAccount
        ManagerAccount oldManagerAccount = customer.getManagerAccount();
        if (oldManagerAccount != null) {
            // Ngắt liên kết với Customer và xóa ManagerAccount cũ
            oldManagerAccount.setCustomer(null);
            customer.setManagerAccount(null);
            managerAccountRepository.delete(oldManagerAccount);
            logger.info("Old ManagerAccount deleted for customerId={}", customerId);
        }

        // 9. Tạo mới ManagerAccount cho Manager
        ManagerAccount newManagerAccount = new ManagerAccount(
            LocalDate.now(),
            ManagerAccount.Status.Confirm,
            savedManager,
            null
        );
        savedManager.setManagerAccount(newManagerAccount); // Liên kết hai chiều
        managerAccountRepository.save(newManagerAccount);
        logger.info("New ManagerAccount created for managerId={}", savedManager.getManagerId());

        // 10. Xóa Customer sau khi đã xử lý
        customerRepository.delete(customer);
        logger.info("Customer deleted: customerId={}", customerId);
    
    }
}
