package com.haven.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.haven.entity.Manager;
import com.haven.entity.ManagerAccount;
import com.haven.entity.ShopSubscription;
import com.haven.entity.SubscriptionPlan;
import com.haven.entity.SubscriptionStatus;
import com.haven.repository.ManagerAccountRepository;
import com.haven.repository.ManagerRepository;
import com.haven.repository.ShopSubscriptionRepository;
import com.haven.repository.SubscriptionPlanRepository;

import jakarta.transaction.Transactional;

@Service
public class ShopSubscriptionService {
    
	private static final Logger logger = LoggerFactory.getLogger(ShopSubscriptionService.class);
	
    @Autowired
    private ShopSubscriptionRepository shopSubscriptionRepository;
    
    @Autowired
    private ManagerRepository managerRepository;
    
    @Autowired
    private ManagerAccountRepository managerAccountRepository;
    
    @Autowired
    private SubscriptionPlanRepository planRepository;
    
    @Autowired
    private MessageNoReplyService messageService;

    // Yêu cầu đăng ký gói
    @Transactional
    public ShopSubscription requestSubscription(Integer managerId, Integer planId) {
        Manager manager = managerRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy quản lý"));

        ManagerAccount managerAccount = managerAccountRepository.findByManager(manager)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản quản lý"));
        
        if (managerAccount.getStatus() == ManagerAccount.Status.Wait) {
            throw new IllegalStateException("Tài khoản đang chờ xác nhận.");
        }
        if (managerAccount.getStatus() == ManagerAccount.Status.Warning) {
            throw new IllegalStateException("Tài khoản đang bị cảnh báo.");
        }

        SubscriptionPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy gói"));

        List<ShopSubscription> activeSubscriptions = shopSubscriptionRepository.findByManagerManagerId(managerId)
            .stream()
            .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE 
                && sub.getEndDate().isAfter(LocalDateTime.now()))
            .toList();
        
        if (!activeSubscriptions.isEmpty()) {
            throw new IllegalStateException("Cửa hàng đã có gói đang hoạt động.");
        }

        ShopSubscription subscription = new ShopSubscription();
        subscription.setManager(manager);
        subscription.setPlan(plan);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getDuration()));
        subscription.setStatus(SubscriptionStatus.PENDING);

        return shopSubscriptionRepository.save(subscription);
    }
    
 // Yêu cầu hủy gói
    @Transactional
    public ShopSubscription requestCancelSubscription(Integer managerId, Integer subscriptionId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quản lý"));

        ManagerAccount managerAccount = managerAccountRepository.findByManager(manager)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản quản lý"));
        
        if (managerAccount.getStatus() == ManagerAccount.Status.Wait) {
            throw new IllegalStateException("Tài khoản đang chờ xác nhận.");
        }
        if (managerAccount.getStatus() == ManagerAccount.Status.Warning) {
            throw new IllegalStateException("Tài khoản đang bị cảnh báo.");
        }

        ShopSubscription subscription = shopSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói đăng ký"));

        // Kiểm tra xem subscription thuộc về manager này không
        if (!(subscription.getManager().getManagerId() == managerId)) {
            throw new IllegalStateException("Bạn không có quyền hủy gói này.");
        }

        // Chỉ cho phép hủy nếu gói đang ở trạng thái PENDING hoặc ACTIVE
        if (subscription.getStatus() != SubscriptionStatus.PENDING && 
            subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Chỉ có thể hủy gói đang ở trạng thái PENDING hoặc ACTIVE.");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        ShopSubscription updatedSubscription = shopSubscriptionRepository.save(subscription);
        logger.info("Subscription cancel requested: managerId={}, subscriptionId={}", 
                managerId, subscriptionId);
        return updatedSubscription;
    }

    // Gia hạn gói
    @Transactional
    public ShopSubscription renewSubscription(Integer managerId, Integer subscriptionId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quản lý"));

        ManagerAccount managerAccount = managerAccountRepository.findByManager(manager)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản quản lý"));
        
        if (managerAccount.getStatus() == ManagerAccount.Status.Wait) {
            throw new IllegalStateException("Tài khoản đang chờ xác nhận.");
        }
        if (managerAccount.getStatus() == ManagerAccount.Status.Warning) {
            throw new IllegalStateException("Tài khoản đang bị cảnh báo.");
        }

        ShopSubscription subscription = shopSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói đăng ký"));

        // Kiểm tra xem subscription thuộc về manager này không
        if (!(subscription.getManager().getManagerId() == managerId)) {
            throw new IllegalStateException("Bạn không có quyền gia hạn gói này.");
        }

        // Chỉ cho phép gia hạn nếu gói đang ACTIVE và gần hết hạn
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Chỉ có thể gia hạn gói đang ở trạng thái ACTIVE.");
        }

        // Kiểm tra xem gói có gần hết hạn không (ví dụ: còn dưới 7 ngày)
        if (subscription.getEndDate().isAfter(LocalDateTime.now().plusDays(7))) {
            throw new IllegalStateException("Chỉ có thể gia hạn khi gói còn dưới 7 ngày nữa hết hạn.");
        }

        // Gia hạn gói bằng cách cập nhật endDate
        SubscriptionPlan plan = subscription.getPlan();
        subscription.setEndDate(subscription.getEndDate().plusDays(plan.getDuration()));
        subscription.setStatus(SubscriptionStatus.PENDING); // Chờ xác nhận gia hạn

        ShopSubscription renewedSubscription = shopSubscriptionRepository.save(subscription);
        logger.info("Subscription renewed: managerId={}, subscriptionId={}, newEndDate={}", 
                managerId, subscriptionId, renewedSubscription.getEndDate());
        return renewedSubscription;
    }
    
    @Scheduled(cron = "0 0 0 * * *") // Chạy lúc 0:00 mỗi ngày
    @Transactional
    public void checkAndNotifySubscriptionExpiration() {
    	logger.info("Bắt đầu kiểm tra gói hết hạn...");
    	
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysFromNow = now.plusDays(3);

        // Lấy các gói ACTIVE sắp hết hạn hoặc đã hết hạn
        List<ShopSubscription> subscriptionsToCheck = shopSubscriptionRepository
            .findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, threeDaysFromNow.plusDays(1)); // Lấy gói hết hạn trong 4 ngày tới

        subscriptionsToCheck.forEach(sub -> {
            LocalDateTime endDate = sub.getEndDate();
            Manager manager = sub.getManager();
            Integer adminId = 1; // Giả sử managerId = 1 là Admin (có thể thay đổi logic lấy Admin)

            if (endDate.isBefore(now)) {
                // Gói đã hết hạn
                sub.setStatus(SubscriptionStatus.EXPIRED);
                shopSubscriptionRepository.save(sub);
                
                messageService.sendMessageToShop(
                    adminId,
                    manager.getManagerId(),
                    "Gói đăng ký đã hết hạn",
                    "Gói " + sub.getPlan().getPlanName() + " của bạn đã hết hạn vào " + endDate + ". Vui lòng gia hạn để tiếp tục sử dụng dịch vụ."
                );
            } else if (endDate.isAfter(now) && endDate.isBefore(threeDaysFromNow)) {
                // Gói sắp hết hạn (trong 3 ngày tới)
                messageService.sendMessageToShop(
                    adminId,
                    manager.getManagerId(),
                    "Gói đăng ký sắp hết hạn",
                    "Gói " + sub.getPlan().getPlanName() + " của bạn sẽ hết hạn vào " + endDate + ". Hãy gia hạn sớm để tránh gián đoạn."
                );
            }
        });
        
        logger.info("Kết thúc kiểm tra, xử lý {} gói.", subscriptionsToCheck.size());
    }

    // Kiểm tra trạng thái gói
    public boolean isSubscriptionActive(Integer shopSubscriptionId) {
        ShopSubscription subscription = shopSubscriptionRepository.findById(shopSubscriptionId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy gói đăng ký"));
        
        return subscription.getStatus() == SubscriptionStatus.ACTIVE 
            && subscription.getEndDate().isAfter(LocalDateTime.now());
    }
}