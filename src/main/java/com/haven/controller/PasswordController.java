package com.haven.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.haven.service.ChangePasswordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api/change")
@Tag(name = "Change password api", description = "Lấy opt đổi password, thay đổi password")
@SecurityRequirement(name = "JWT")
public class PasswordController {

	private static final Logger logger = LoggerFactory.getLogger(ChangePasswordService.class);

    @Autowired
    private ChangePasswordService passwordService;

    @PostMapping("/request-otp")
    @Operation(
		    summary = "Lấy OTP"
		)
    public ResponseEntity<String> requestOtp(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Token không hợp lệ");
            }
            String token = authorizationHeader.substring(7);
            passwordService.requestOtpForPasswordChange(token);
            return ResponseEntity.ok("Đã gửi mã OTP đến email của bạn");
        } catch (IllegalArgumentException e) {
            logger.warn("Yêu cầu OTP thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi khi yêu cầu OTP: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    @Operation(
		    summary = "Thay đổi password"
		)
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authorizationHeader,
                                                 @RequestBody Map<String, String> request) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Token không hợp lệ");
            }
            String token = authorizationHeader.substring(7);
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            String otp = request.get("otp");

            if (oldPassword == null || newPassword == null || otp == null) {
                throw new IllegalArgumentException("Thiếu thông tin cần thiết");
            }

            passwordService.changePassword(token, oldPassword, newPassword, otp);
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (IllegalArgumentException e) {
            logger.warn("Đổi mật khẩu thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi khi đổi mật khẩu: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }
}