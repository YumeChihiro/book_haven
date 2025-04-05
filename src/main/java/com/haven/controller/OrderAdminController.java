package com.haven.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.AdminCommissionStatsDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.OrderAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Stats admin api", description = "Thống kê hoa hồng cho admin")
@SecurityRequirement(name = "JWT")
public class OrderAdminController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private OrderAdminService orderAdminService;
	
	@GetMapping("/commission-stats")
	 @Operation(
			    summary = "Thống kê hoa hồng"
			)
	public ResponseEntity<AdminCommissionStatsDTO> getAdminCommissionStats(
			@RequestHeader("Authorization") String token) {
		
		String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
	    AdminCommissionStatsDTO stats = orderAdminService.getAdminCommissionStats(userId);
	    return ResponseEntity.ok(stats);
	}
}
