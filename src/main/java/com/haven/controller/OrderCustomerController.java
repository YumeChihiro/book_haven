package com.haven.controller;

import com.haven.dto.OrderRequestDTO;
import com.haven.dto.OrderResponseDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.OrderCartService;
import com.haven.service.OrderCustomerService;
import com.haven.service.OrderHomeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/orders")
@Tag(name = "Order customer api", description = "Xem, mua, hủy, lịch sử cho order của customer")
public class OrderCustomerController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
    private OrderCartService orderCartService;
	
	@Autowired
	private OrderHomeService orderHomeService;
	
	@Autowired
	private OrderCustomerService orderCustomerService;

    @PostMapping("/checkout")
    @Operation(
		    summary = "Mua hàng từ giỏ hàng (mua tất cả hoặc chọn sản phẩm cụ thể)"
		)
    public ResponseEntity<List<OrderResponseDTO>> checkoutFromCart(@RequestBody OrderRequestDTO orderRequest) {
        List<OrderResponseDTO> responses = orderCartService.checkoutFromCart(orderRequest);
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/buy-from-home")
    @Operation(
		    summary = "Mua từ home (sản phẩm cụ thể)"
		)
    public ResponseEntity<OrderResponseDTO> buyProductFromHome(@RequestBody OrderRequestDTO orderRequest) {
        OrderResponseDTO response = orderHomeService.buyProductFromHome(orderRequest);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{orderId}")
    @Operation(
		    summary = "Xem chi tiết đơn hàng"
		)
    public ResponseEntity<OrderResponseDTO> getOrderDetails(
            @PathVariable Integer orderId,
            @RequestHeader("Authorization") String token) {
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        OrderResponseDTO response = orderCustomerService.getOrderDetails(orderId, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{orderId}/cancel")
    @Operation(
		    summary = "Hủy đơn hàng"
		)
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable Integer orderId,
            @RequestHeader("Authorization") String token) {
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        OrderResponseDTO response = orderCustomerService.cancelOrder(orderId, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    @Operation(
		    summary = "Lịch sử đơn hàng"
		)
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
    		   @RequestHeader("Authorization") String token) {
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        List<OrderResponseDTO> responses = orderCustomerService.getOrderHistory(userId);
        return ResponseEntity.ok(responses);
    }
}