package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.SendVoucherRequest;
import com.haven.dto.VoucherDTO;
import com.haven.entity.Manager;
import com.haven.securities.CustomerUserDetail;
import com.haven.service.VoucherShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/shop/vouchers")
@Tag(name = "Admin account api", description = "Khóa, mở khóa, xóa tài khoản")
@SecurityRequirement(name = "JWT")
public class VoucherShopController {
	
	@Autowired
	private VoucherShopService voucherShopService;

    @GetMapping
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Lấy voucher mà admin đã gửi"
		)
    public ResponseEntity<List<VoucherDTO>> getShopVouchers(
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager shop = userDetails.getManager();
        if (shop == null) {
            throw new RuntimeException("Only shops can view vouchers");
        }
        
        List<VoucherDTO> vouchers = voucherShopService.getVouchersForShop(shop.getManagerId());
        return ResponseEntity.ok(vouchers);
    }

    // Gửi voucher cho một customer dựa trên giỏ hàng
    @PostMapping("/send-to-customer-by-cart/{customerId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Gửi voucher cho customer (customer có giỏ hàng chứa hàng của shop)"
		)
    public ResponseEntity<String> sendVoucherToCustomerByCart(
            @PathVariable Integer customerId,
            @RequestBody SendVoucherRequest request,
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager shop = userDetails.getManager();
        if (shop == null) {
            throw new RuntimeException("Only shops can send vouchers");
        }
        
        try {
        	voucherShopService.sendVoucherToCustomerByCart(
                shop.getManagerId(),
                customerId,
                request.getVoucherCode()
            );
            return ResponseEntity.ok("Voucher sent to customer successfully (by cart)");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Gửi voucher cho một customer dựa trên đơn hàng
    @PostMapping("/send-to-customer-by-order/{customerId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Gửi voucher cho customer (customer có đơn hàng của shop)"
		)
    public ResponseEntity<String> sendVoucherToCustomerByOrder(
            @PathVariable Integer customerId,
            @RequestBody SendVoucherRequest request,
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager shop = userDetails.getManager();
        if (shop == null) {
            throw new RuntimeException("Only shops can send vouchers");
        }
        
        try {
        	voucherShopService.sendVoucherToCustomerByOrder(
                shop.getManagerId(),
                customerId,
                request.getVoucherCode()
            );
            return ResponseEntity.ok("Voucher sent to customer successfully (by order)");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Gửi voucher cho tất cả customer dựa trên giỏ hàng
    @PostMapping("/send-to-all-by-cart")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Gửi voucher cho tất cả customer (dựa trên giỏ hàng)"
		)
    public ResponseEntity<String> sendVoucherToAllCustomersByCart(
            @RequestBody SendVoucherRequest request,
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager shop = userDetails.getManager();
        if (shop == null) {
            throw new RuntimeException("Only shops can send vouchers");
        }
        
        try {
        	voucherShopService.sendVoucherToAllCustomersByCart(
                shop.getManagerId(),
                request.getVoucherCode()
            );
            return ResponseEntity.ok("Voucher sent to all customers successfully (by cart)");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Gửi voucher cho tất cả customer dựa trên đơn hàng
    @PostMapping("/send-to-all-by-order")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Gửi voucher cho tất cả customer (dựa trên đơn hàng)"
		)
    public ResponseEntity<String> sendVoucherToAllCustomersByOrder(
            @RequestBody SendVoucherRequest request,
            @AuthenticationPrincipal CustomerUserDetail userDetails) {
        Manager shop = userDetails.getManager();
        if (shop == null) {
            throw new RuntimeException("Only shops can send vouchers");
        }
        
        try {
        	voucherShopService.sendVoucherToAllCustomersByOrder(
                shop.getManagerId(),
                request.getVoucherCode()
            );
            return ResponseEntity.ok("Voucher sent to all customers successfully (by order)");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
