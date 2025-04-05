package com.haven.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.exception.ResourceNotFoundException;
import com.haven.securities.JwtUtil;
import com.haven.service.UpgradeCustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customerUpgrade")
@Tag(name = "Upgrafe customer ", description = "Nâng cấp tài khoản customer lên shop")
@SecurityRequirement(name = "JWT")
public class UpgradeCustomerController{
    
	private final UpgradeCustomerService upgradeCustomerService;
	private final JwtUtil jwtUtil;

    public UpgradeCustomerController(UpgradeCustomerService upgradeCustomerService, JwtUtil jwtUtil) {
        this.upgradeCustomerService = upgradeCustomerService;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping("/upgrade-to-manager")
    @PreAuthorize("isAuthenticated() and principal.id == #customerId")
    @Operation(
		    summary = "Nâng cấp customer lên shop"
		)
    public ResponseEntity<?> upgradeCustomerToManager(
    		 @RequestHeader("Authorization") String token,
            Authentication authentication) {
        try {
        	String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);
            upgradeCustomerService.upgradeCustomerToManager(userId, authentication);
            return ResponseEntity.ok("Đã nâng cấp Customer thành Manager với role SHOP thành công");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi nâng cấp Customer: " + e.getMessage());
        }
    }
}