package com.haven.controller;

import com.haven.dto.AddToCartRequestDTO;
import com.haven.dto.CartResponseDTO;
import com.haven.securities.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.haven.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/customer/cart")
@Tag(name = "Cart api", description = "Crud giỏ hàng của customer")
@SecurityRequirement(name = "JWT")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add")
    @Operation(
		    summary = "Thêm sản phẩm vào giỏ hàng"
		)
    public ResponseEntity<String> addToCart(
            @RequestBody AddToCartRequestDTO requestDTO,
            HttpServletRequest request) {
        Integer customerId = getCustomerIdFromToken(request);
        if (customerId == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        cartService.addProductToCart(customerId, requestDTO.getProductId(), requestDTO.getQuantity());
        return ResponseEntity.ok("Product added to cart successfully");
    }
    
    @GetMapping("/view")
    @Operation(
		    summary = "Xem giỏ hàng"
		)
    public ResponseEntity<CartResponseDTO> viewCart(HttpServletRequest request) {
        Integer customerId = getCustomerIdFromToken(request);
        if (customerId == null) {
            return ResponseEntity.status(401).body(null);
        }
        CartResponseDTO response = cartService.getCart(customerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cart/increase/{cartItemId}")
    @Operation(
		    summary = "Thêm số lượng sản phẩm"
		)
    public ResponseEntity<CartResponseDTO> increaseCartItem(
            @PathVariable Integer cartItemId,
            @RequestParam Integer quantity,
            HttpServletRequest request) {
        Integer customerId = getCustomerIdFromToken(request);
        if (customerId == null) {
            return ResponseEntity.status(401).body(null);
        }
        CartResponseDTO response = cartService.increaseCartItemQuantity(customerId, cartItemId, quantity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cart/decrease/{cartItemId}")
    @Operation(
		    summary = "Giảm số lượng sản phẩm"
		)
    public ResponseEntity<CartResponseDTO> decreaseOrRemoveCartItem(
            @PathVariable Integer cartItemId,
            @RequestParam Integer quantity,
            HttpServletRequest request) {
        Integer customerId = getCustomerIdFromToken(request);
        if (customerId == null) {
            return ResponseEntity.status(401).body(null);
        }
        CartResponseDTO response = cartService.decreaseOrRemoveCartItem(customerId, cartItemId, quantity);
        return ResponseEntity.ok(response);
    }

    private Integer getCustomerIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        try {
            return jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }
  
}