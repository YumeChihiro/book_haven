package com.haven.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.haven.entity.ManagerAccount;
import com.haven.entity.ManagerAccount.Status;
import com.haven.repository.ManagerAccountRepository;

@Service
public class AccountService {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final ManagerAccountRepository managerAccountRepository;

    public AccountService(ManagerAccountRepository managerAccountRepository) {
        this.managerAccountRepository = managerAccountRepository;
    }
    
    @Scheduled(cron = "0 0 0 * * ?") // Chạy hàng ngày lúc 0h
    public void autoDeleteInactiveAccounts() {
        deleteAllInactiveAccounts();
    }

    @Transactional
    public void lockAccountTemporarily(Integer accountId, String reason) {
        ManagerAccount account = managerAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if (account.getStatus() == Status.Locked) {
            throw new IllegalStateException("Tài khoản đã bị khóa trước đó.");
        }

        account.setStatus(Status.Locked);
        // Có thể thêm trường lockedUntil nếu muốn khóa tạm thời có thời hạn
        managerAccountRepository.save(account);
        logger.info("Tài khoản {} đã bị khóa tạm thời. Lý do: {}", accountId, reason);
    }

    @Transactional
    public void unlockAccount(Integer accountId) {
        ManagerAccount account = managerAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if (account.getStatus() != Status.Locked) {
            throw new IllegalStateException("Tài khoản không ở trạng thái bị khóa.");
        }

        account.setStatus(Status.Confirm); // Quay lại trạng thái bình thường
        managerAccountRepository.save(account);
        logger.info("Tài khoản {} đã được mở khóa.", accountId);
    }
    
 // Xóa tài khoản không hoạt động quá 1 năm
    @Transactional
    public void deleteInactiveAccount(Integer accountId) {
        ManagerAccount account = managerAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        if (account.getRecentActivity().isAfter(oneYearAgo)) {
            throw new IllegalStateException("Tài khoản vẫn còn hoạt động trong vòng 1 năm qua.");
        }

        // Xóa tài khoản (cascade sẽ xóa Manager hoặc Customer nếu được cấu hình)
        managerAccountRepository.delete(account);
        logger.info("Tài khoản {} đã bị xóa vĩnh viễn vì không hoạt động quá 1 năm.", accountId);
    }

    // Xóa hàng loạt tài khoản không hoạt động quá 1 năm
    @Transactional
    public void deleteAllInactiveAccounts() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<ManagerAccount> inactiveAccounts = managerAccountRepository.findByRecentActivity(oneYearAgo);

        if (inactiveAccounts.isEmpty()) {
            logger.info("Không có tài khoản nào không hoạt động quá 1 năm để xóa.");
            return;
        }

        managerAccountRepository.deleteAll(inactiveAccounts);
        logger.info("Đã xóa {} tài khoản không hoạt động quá 1 năm.", inactiveAccounts.size());
    }
}
