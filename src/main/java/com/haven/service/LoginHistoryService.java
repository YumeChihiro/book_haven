package com.haven.service;

import com.haven.entity.Customer;
import com.haven.entity.LoginHistory;
import com.haven.entity.Manager;
import com.haven.entity.Message;
import com.haven.repository.LoginHistoryRepository;
import com.haven.repository.ManagerRepository;
import com.haven.repository.MessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class LoginHistoryService {

    private static final Logger logger = Logger.getLogger(LoginHistoryService.class.getName());

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private static final int ADMIN_MANAGER_ID = 1; // ID của Admin

    @Transactional
    public void saveLoginHistory(HttpServletRequest request, Customer customer, Manager manager) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest không được null");
        }
        if (customer == null && manager == null) {
            throw new IllegalArgumentException("Phải cung cấp ít nhất một Customer hoặc Manager");
        }

        // Tạo bản ghi LoginHistory
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setCustomer(customer);
        loginHistory.setManager(manager);
        loginHistory.setIpAddress(getClientIp(request));
        loginHistory.setDeviceInfo(getDeviceInfo(request));
        loginHistory.setLoginTime(LocalDateTime.now());

        loginHistoryRepository.save(loginHistory);
        logger.info("Đã lưu lịch sử đăng nhập cho " + (customer != null ? "Customer: " + customer.getEmail() : "Manager: " + manager.getEmail()));

        // Kiểm tra đăng nhập bất thường và gửi thông báo
        checkAndNotifyAbnormalLogin(loginHistory);
    }

    private void checkAndNotifyAbnormalLogin(LoginHistory login) {
        if (isAbnormalLogin(login)) {
            Manager admin = managerRepository.findById(ADMIN_MANAGER_ID)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Admin"));

            Message message = new Message();
            message.setTitle("Cảnh báo đăng nhập bất thường");
            message.setContent("Phát hiện đăng nhập từ IP: " + login.getIpAddress() +
                              " trên thiết bị: " + login.getDeviceInfo() + " vào lúc " + login.getLoginTime());
            message.setSender(admin);

            if (login.getCustomer() != null) {
                message.setCustomer(login.getCustomer());
                logger.info("Gửi thông báo đến Customer: " + login.getCustomer().getEmail());
            } else if (login.getManager() != null) {
                message.setManager(login.getManager());
                logger.info("Gửi thông báo đến Manager: " + login.getManager().getEmail());
            }

            messageRepository.save(message);
        }
    }

    private boolean isAbnormalLogin(LoginHistory login) {
        List<LoginHistory> history;
        int userId;

        if (login.getCustomer() != null) {
            userId = login.getCustomer().getCustomerId();
            history = loginHistoryRepository.findByCustomerCustomerId(userId);
        } else if (login.getManager() != null) {
            userId = login.getManager().getManagerId();
            history = loginHistoryRepository.findByManagerManagerId(userId);
        } else {
            return false;
        }

        // Loại bản ghi hiện tại ra khỏi lịch sử
        history.removeIf(h -> h.getLoginId().equals(login.getLoginId()));

        // Đăng nhập lần đầu (history rỗng) hoặc IP/thiết bị lạ
        return history.isEmpty() || history.stream()
            .noneMatch(h -> h.getIpAddress().equals(login.getIpAddress()) && 
                           (h.getDeviceInfo() != null && h.getDeviceInfo().equals(login.getDeviceInfo())));
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress != null ? ipAddress : "unknown";
    }

    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }
}