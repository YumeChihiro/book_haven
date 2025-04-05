package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.ManagerOrderStatsDTO;
import com.haven.dto.OrderResponseDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.OrderShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/shop/orders")
@Tag(name = "Order shop api", description = "Thống kê, cập nhật đơn hàng của shop")
@SecurityRequirement(name = "JWT")
public class OrderShopController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private OrderShopService orderShopService;
	
	// Xem danh sách đơn hàng của shop
	@GetMapping("/view")
	  @Operation(
			    summary = "Xem danh sách đơn hàng của shop"
			)
	public ResponseEntity<List<OrderResponseDTO>> getShopOrders(
	        @RequestHeader("Authorization") String token) {
		
		String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
	    List<OrderResponseDTO> responses = orderShopService.getShopOrders(userId);
	    return ResponseEntity.ok(responses);
	}
	
	// Xem chi tiết đơn hàng của shop
	@GetMapping("/{orderId}")
	  @Operation(
			    summary = "Xem chi tiết đơn hàng của shop"
			)
	public ResponseEntity<OrderResponseDTO> getShopOrderDetails(
	        @PathVariable Integer orderId,
	        @RequestHeader("Authorization") String token) {
		
		String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
	    OrderResponseDTO response = orderShopService.getShopOrderDetails(orderId, userId);
	    return ResponseEntity.ok(response);
	}

	// Cập nhật trạng thái đơn hàng
	@PutMapping("/{orderId}/status")
	  @Operation(
			    summary = "Cập nhật trạng thái đơn hàng"
			)
	public ResponseEntity<OrderResponseDTO> updateOrderStatus(
	        @PathVariable Integer orderId,
	        @RequestHeader("Authorization") String token,
	        @RequestParam Integer newStatusId) {
		
		String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
	    OrderResponseDTO response = orderShopService.updateOrderStatus(orderId, userId, newStatusId);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/stats")
	  @Operation(
			    summary = "Thống kê lợi nhuận của shop"
			)
	public ResponseEntity<ManagerOrderStatsDTO> getManagerOrderStats(
			 @RequestHeader("Authorization") String token) {
		
		String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
	    ManagerOrderStatsDTO stats = orderShopService.getManagerOrderStats(userId);
	    return ResponseEntity.ok(stats);
	}
}
