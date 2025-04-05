package com.haven.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.AuthResponse;
import com.haven.dto.EmailRequest;
import com.haven.dto.LoginRequest;
import com.haven.dto.RegisterRequest;
import com.haven.dto.RegisterResponse;
import com.haven.dto.VerifyRequest;
import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.exception.EmailNotVerifiedException;
import com.haven.securities.JwtUtil;
import com.haven.service.AuthService;
import com.haven.service.CustomerService;
import com.haven.service.LoginHistoryService;
import com.haven.service.ManagerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth api", description = "Đăng kí, đăng nhập, xác thực email, lịch sử đăng nhập, kiểm tra đăng nhập bất thường")
@SecurityRequirements()
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;
	
	@Autowired
	private LoginHistoryService loginHistoryService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ManagerService managerService;

	@PostMapping("/login")
	@Operation(
		    summary = "Đăng nhập, lưu lịch sử, thông báo đăng nhập bất thường (account admin: admin@gmail.com / 123456"
		)
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
	    logger.info("Nhận yêu cầu đăng nhập - Email: " + request.getEmail());

	    try {
	        AuthResponse response = authService.authenticate(request);
	        String token = response.getToken();
	       
	        String roles = jwtUtil.getRolesFromToken(token);  
	        Integer userId = jwtUtil.extractUserId(token); 

	        // Xác định Customer hoặc Manager
	        Customer customer = null;
	        Manager manager = null;
	        if (roles.contains("CUSTOMER")) {
	            customer = customerService.findById(userId);
	        } else if (roles.contains("SHOP")) {
	            manager = managerService.findById(userId);
	        }
	        
	        if(roles.contains("ADMIN")) {
	        	return ResponseEntity.ok(response);
	        }

	        // Lưu lịch sử đăng nhập và kiểm tra bất thường
	        loginHistoryService.saveLoginHistory(httpRequest, customer, manager);

	        return ResponseEntity.ok(response);
	        
	    } catch (BadCredentialsException e) {
	        logger.warn("Thông tin đăng nhập sai cho email: " + request.getEmail());
	        return ResponseEntity.status(401).body("Sai email hoặc mật khẩu!");
	        
	    } catch (EmailNotVerifiedException e) {
	        logger.warn("Email chưa xác minh cho: " + request.getEmail());
	        Map<String, String> response = new HashMap<>();
	        response.put("message", e.getMessage());
	        response.put("verifyUrl", "/api/auth/verify-email");
	        return ResponseEntity.status(403).body(response);
	    }catch (IllegalStateException e) {
	    	return ResponseEntity.status(401).body("Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên" +e);
	        
	    } catch (Exception e) {
	        logger.error("Lỗi xác thực: " + e.getMessage(), e);
	        return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
	    }
	}

	@PostMapping("/register")
	@Operation(
		    summary = "Đăng kí"
		)
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		logger.info("Nhận yêu cầu đăng ký - Email: " + request.getEmail());

		try {
			RegisterResponse response = authService.register(request);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			logger.warn("Đăng ký thất bại: " + e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			logger.error("Lỗi đăng ký: " + e.getMessage(), e);
			return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
		}
	}

	@PostMapping("/verify-email")
	@Operation(
		    summary = "Lấy mã OTP để xác thực email"
		)
	public ResponseEntity<?> requestEmailVerification(@RequestBody EmailRequest request) {
		try {
			authService.sendVerificationEmail(request.getEmail());
			return ResponseEntity.ok("Mã OTP đã được gửi tới email của bạn!");
		} catch (Exception e) {
			logger.error("Lỗi gửi email xác minh: " + e.getMessage(), e);
			return ResponseEntity.status(500).body("Lỗi gửi email xác minh: " + e.getMessage());
		}
	}

	@PostMapping("/verify")
	@Operation(
		    summary = "Xác minh email với mã OPT đã được cấp"
		)
	public ResponseEntity<?> verifyEmail(@RequestBody VerifyRequest request) {
		try {
			boolean verified = authService.verifyEmail(request.getEmail(), request.getOtp());
			if (verified) {
				return ResponseEntity.ok("Email đã được xác minh thành công!");
			}
			return ResponseEntity.status(400).body("OTP không hợp lệ hoặc đã hết hạn!");
		} catch (Exception e) {
			logger.error("Lỗi xác minh email: " + e.getMessage(), e);
			return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
		}
	}
}