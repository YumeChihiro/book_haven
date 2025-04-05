package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.SubscriptionPlanRequest;
import com.haven.dto.SubscriptionRegistrationRequest;
import com.haven.entity.ShopSubscription;
import com.haven.entity.SubscriptionBenefit;
import com.haven.entity.SubscriptionPlan;
import com.haven.securities.JwtUtil;
import com.haven.service.AdminSubscriptionService;
import com.haven.service.ShopSubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscription api", description = "Xem, đăng kí, hủy, duyệt gói đăng kí")
@SecurityRequirement(name = "JWT")
public class SubscriptionController {
    
    @Autowired
    private AdminSubscriptionService adminService;
    
    @Autowired
    private ShopSubscriptionService shopService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // Lấy tất cả các gói (công khai)
    @GetMapping("/plans")
    @Operation(
		    summary = "Lấy thông tin tất cả các gói"
		)
    public ResponseEntity<List<SubscriptionPlan>> getAllPlans() {
        return ResponseEntity.ok(adminService.getAllPlans());
    }

    // Lấy lợi ích của một gói (công khai)
    @GetMapping("/plans/{planId}/benefits")
    @Operation(
		    summary = "Lấy lợi lích của gói"
		)
    public ResponseEntity<List<SubscriptionBenefit>> getPlanBenefits(@PathVariable Integer planId) {
        return ResponseEntity.ok(adminService.getPlanBenefits(planId));
    }

    // Admin tạo gói mới
    @PostMapping("/plans")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Admin tạo gói mới"
		)
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlanRequest request) {
        SubscriptionPlan plan = adminService.createPlan(
            request.getPlanName(),
            request.getPrice(),
            request.getDuration(),
            request.getBenefits()
        );
        return ResponseEntity.ok(plan);
    }

    // Admin duyệt gói
    @PostMapping("/approve/{subscriptionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Admin duyệt đăng kí gói của shop"
		)
    public ResponseEntity<ShopSubscription> approveSubscription(@PathVariable Integer subscriptionId) {
        ShopSubscription subscription = adminService.approveSubscription(subscriptionId);
        return ResponseEntity.ok(subscription);
    }

    // Shop yêu cầu đăng ký gói
    @PostMapping("/request")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Shop yêu cầu đăng ký gói"
		)
    public ResponseEntity<ShopSubscription> requestSubscription(
            @RequestBody SubscriptionRegistrationRequest request,
            @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Integer managerId = jwtUtil.extractUserId(jwt);
        ShopSubscription subscription = shopService.requestSubscription(managerId, request.getPlanId());
        return ResponseEntity.ok(subscription);
    }
    
 // Yêu cầu hủy gói
    @PostMapping("/cancel/{subscriptionId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Shop yêu cầu hủy gói"
		)
    public ResponseEntity<ShopSubscription> requestCancelSubscription(
            @PathVariable Integer subscriptionId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer managerId = jwtUtil.extractUserId(jwt);
            ShopSubscription subscription = shopService.requestCancelSubscription(managerId, subscriptionId);
            return ResponseEntity.ok(subscription);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/renew/{subscriptionId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Shop yêu cầu gia hạn gói"
		)
    public ResponseEntity<?> renewSubscription(
            @PathVariable Integer subscriptionId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer managerId = jwtUtil.extractUserId(jwt);
            ShopSubscription subscription = shopService.renewSubscription(managerId, subscriptionId);
            return ResponseEntity.ok(subscription);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    // Kiểm tra trạng thái gói
    @GetMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(
		    summary = "Kiểm tra trạng thái gói"
		)
    public ResponseEntity<Boolean> checkSubscriptionStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(shopService.isSubscriptionActive(id));
    }
}