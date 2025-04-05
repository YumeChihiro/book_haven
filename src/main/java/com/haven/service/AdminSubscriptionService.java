package com.haven.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haven.dto.SubscriptionBenefitDTO;
import com.haven.entity.ShopSubscription;
import com.haven.entity.SubscriptionBenefit;
import com.haven.entity.SubscriptionPlan;
import com.haven.entity.SubscriptionStatus;
import com.haven.repository.ShopSubscriptionRepository;
import com.haven.repository.SubscriptionBenefitRepository;
import com.haven.repository.SubscriptionPlanRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminSubscriptionService {
    
    @Autowired
    private SubscriptionPlanRepository planRepository;
    
    @Autowired
    private ShopSubscriptionRepository shopSubscriptionRepository;
    
    @Autowired
    private SubscriptionBenefitRepository benefitRepository;

    // Tạo gói mới với các lợi ích
    @Transactional
    public SubscriptionPlan createPlan(String planName, Float price, Integer duration, List<SubscriptionBenefitDTO> benefits) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setPlanName(planName);
        plan.setPrice(price);
        plan.setDuration(duration);

        // Thêm các lợi ích
        if (benefits != null && !benefits.isEmpty()) {
            benefits.forEach(dto -> {
                SubscriptionBenefit benefit = new SubscriptionBenefit();
                benefit.setBenefitType(dto.getBenefitType());
                benefit.setBenefitValue(dto.getBenefitValue());
                plan.addBenefit(benefit); // Sử dụng liên kết ngược
            });
        }

        return planRepository.save(plan);
    }

    // Duyệt gói đăng ký
    @Transactional
    public ShopSubscription approveSubscription(Integer subscriptionId) {
        ShopSubscription subscription = shopSubscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy gói đăng ký"));
        
        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new IllegalStateException("Gói không ở trạng thái chờ duyệt.");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return shopSubscriptionRepository.save(subscription);
    }

    // Lấy danh sách tất cả các gói (bao gồm lợi ích)
    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    // Lấy danh sách lợi ích của một gói
    public List<SubscriptionBenefit> getPlanBenefits(Integer planId) {
        return benefitRepository.findByPlanId(planId);
    }
}
