package com.haven.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.VoucherRequest;
import com.haven.entity.Manager;
import com.haven.entity.Voucher;
import com.haven.securities.CustomerUserDetail;
import com.haven.service.VoucherAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/vouchers")
@Tag(name = "Voucher admin api", description = "Tạo, gửi voucher")
@SecurityRequirement(name = "JWT")
public class VoucherAdminController {

    @Autowired
    private VoucherAdminService voucherService; 

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Admin tạo voucher"
		)
    public ResponseEntity<Voucher> createVoucher(
            @RequestBody VoucherRequest request,
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager admin = userDetails.getManager();
        if (admin == null) {
            throw new RuntimeException("Only managers can create vouchers");
        }
        Voucher voucher = voucherService.createVoucherForPremiumPlan(
            request.getVoucherCode(),
            request.getDiscountPercentage(),
            request.getExpirationDate(),
            admin.getManagerId()
        );
        return ResponseEntity.ok(voucher);
    }

    @PostMapping("/send-to-all-premium-shops")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Gửi voucher cho shop có gói đăng kí"
		)
    public ResponseEntity<String> sendVoucherToAllPremiumShops(
            @RequestBody VoucherRequest request,
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager admin = userDetails.getManager();
        if (admin == null) {
            throw new RuntimeException("Only managers can send vouchers");
        }
        try {
            voucherService.sendVoucherToAllPremiumShops(
                admin.getManagerId(),
                request.getVoucherCode(),
                request.getDiscountPercentage(),
                request.getExpirationDate()
            );
            return ResponseEntity.ok("Voucher created and sent to all premium shops successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

}