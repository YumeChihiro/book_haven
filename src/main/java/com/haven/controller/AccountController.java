package com.haven.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haven.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/lock")
@Tag(name = "Admin account api", description = "Khóa, mở khóa, xóa tài khoản")
@SecurityRequirement(name = "JWT")
public class AccountController {
	
	private final AccountService managerAccountService;

    public AccountController(AccountService managerAccountService) {
        this.managerAccountService = managerAccountService;
    }

    @PostMapping("/lock/{accountId}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin có quyền khóa
    @Operation(
		    summary = "Khóa tài khoản theo id (id: 3)"
		)
    public ResponseEntity<String> lockAccount(
            @PathVariable Integer accountId,
            @RequestParam String reason) {
        try {
            managerAccountService.lockAccountTemporarily(accountId, reason);
            return ResponseEntity.ok("Tài khoản đã bị khóa tạm thời.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi khóa tài khoản: " + e.getMessage());
        }
    }

    @PostMapping("/unlock/{accountId}")
    @Operation(
		    summary = "Lấy id của tài khoản cần mở khóa (id: 3)"
		)
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin có quyền mở khóa
    public ResponseEntity<String> unlockAccount(
            @PathVariable Integer accountId) {
        try {
            managerAccountService.unlockAccount(accountId);
            return ResponseEntity.ok("Tài khoản đã được mở khóa.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi mở khóa tài khoản: " + e.getMessage());
        }
    }
    
 // Xóa một tài khoản không hoạt động quá 1 năm
    @DeleteMapping("/delete/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Lấy id của tài khoản cần xóa (id test: 3)"
		)
    public ResponseEntity<String> deleteInactiveAccount(
            @PathVariable Integer accountId) {
        try {
            managerAccountService.deleteInactiveAccount(accountId);
            return ResponseEntity.ok("Tài khoản đã bị xóa vĩnh viễn.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

    // Xóa tất cả tài khoản không hoạt động quá 1 năm
    @DeleteMapping("/delete-inactive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xóa tất cả tài khoản không hoạt động quá 1 năm"
		)
    public ResponseEntity<String> deleteAllInactiveAccounts() {
        try {
            managerAccountService.deleteAllInactiveAccounts();
            return ResponseEntity.ok("Đã xóa tất cả tài khoản không hoạt động quá 1 năm.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }
}
