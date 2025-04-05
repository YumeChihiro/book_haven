package com.haven.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haven.entity.ShopSubscription;
import com.haven.entity.SubscriptionStatus;
import com.haven.entity.Voucher;
import com.haven.repository.ShopSubscriptionRepository;
import com.haven.repository.VoucherRepository;

import jakarta.transaction.Transactional;

@Service
public class VoucherAdminService {
    
    @Autowired
    private VoucherRepository voucherRepository;
    
    @Autowired
    private ShopSubscriptionRepository shopSubscriptionRepository;
    
    @Autowired
    private MessageNoReplyService messageService;
    
    private static final String PREMIUM_PLAN_NAME = "Cao cấp";
    
    @Transactional
    public Voucher createVoucherForPremiumPlan(String voucherCode, float discountPercentage, 
            LocalDate expirationDate, Integer adminId) {
        Voucher voucher = new Voucher();
        voucher.setVoucherCode(voucherCode);
        voucher.setDiscountPercentage(discountPercentage);
        voucher.setCreatedAt(LocalDate.now());
        voucher.setExpirationDate(expirationDate);
        
        return voucherRepository.save(voucher);
    }
    
    @Transactional
    public void sendVoucherToAllPremiumShops(Integer adminId, String voucherCode, 
            float discountPercentage, LocalDate expirationDate) {
        // Create voucher
        Voucher voucher = createVoucherForPremiumPlan(voucherCode, discountPercentage, expirationDate, adminId);
        
        // Get all active premium shops
        List<ShopSubscription> premiumSubscriptions = shopSubscriptionRepository
            .findByPlanPlanNameAndStatusAndEndDateAfter(
                PREMIUM_PLAN_NAME, 
                SubscriptionStatus.ACTIVE, 
                LocalDateTime.now()
            );
        
        if (premiumSubscriptions.isEmpty()) {
            throw new RuntimeException("Không có shop nào đang sử dụng gói Cao cấp còn hạn");
        }
        
        // Create message content
        String title = "New Voucher Available";
        String content = String.format(
            "A new voucher has been created for Premium Plan users:\n" +
            "Voucher Code: %s\n" +
            "Discount: %.2f%%\n" +
            "Expiration Date: %s",
            voucher.getVoucherCode(),
            voucher.getDiscountPercentage(),
            voucher.getExpirationDate().toString()
        );
        
        // Send message to all premium shops
        for (ShopSubscription subscription : premiumSubscriptions) {
            messageService.sendMessageToShop(
                adminId, 
                subscription.getManager().getManagerId(), 
                title, 
                content
            );
        }
    }
    
    
    
    
    
}
