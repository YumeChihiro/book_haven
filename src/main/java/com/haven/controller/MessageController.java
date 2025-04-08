package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.MessageRequest;
import com.haven.entity.Message;
import com.haven.securities.JwtUtil;
import com.haven.service.MessageNoReplyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message api", description = "Admin và shop gửi tin nhắn, shop và customer xem tin nhắn. Đánh dấu đã đọc")
public class MessageController {

    @Autowired
    private MessageNoReplyService messageService;

    @Autowired
    private JwtUtil jwtUtil;

    // Shop gửi thông báo cho Customer
    @PostMapping("/shop-to-customer")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Shop gửi tin nhắn cho customer"
		)
    public ResponseEntity<Message> sendMessageToCustomer(
            @RequestBody MessageRequest request,
            @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Integer senderManagerId = jwtUtil.extractUserId(jwt);
        Message message = messageService.sendMessageToCustomer(senderManagerId, request.getCustomerId(), 
            request.getTitle(), request.getContent());
        return ResponseEntity.ok(message);
    }

    // Admin gửi thông báo cho Shop
    @PostMapping("/admin-to-shop")
    @Operation(
		    summary = "Admin gửi tin nhắn cho shop"
		)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Message> sendMessageToShop(
            @RequestBody MessageRequest request,
            @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Integer senderManagerId = jwtUtil.extractUserId(jwt);
        Message message = messageService.sendMessageToShop(senderManagerId, request.getManagerId(), 
            request.getTitle(), request.getContent());
        return ResponseEntity.ok(message);
    }

    // Customer xem hộp thư
    @GetMapping("/customer")
    @Operation(
		    summary = "Customer xem hộp thư"
		)
    @PreAuthorize("isAuthenticated()") // Giả sử Customer cũng dùng JWT
    public ResponseEntity<List<Message>> getCustomerMessages(
            @RequestParam("customerId") Integer customerId) {
        return ResponseEntity.ok(messageService.getCustomerMessages(customerId));
    }

    // Shop xem hộp thư
    @GetMapping("/shop")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Shop xem hộp thư"
		)
    public ResponseEntity<List<Message>> getShopMessages(
            @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Integer managerId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(messageService.getShopMessages(managerId));
    }

    // Đánh dấu tin nhắn đã đọc
    @PostMapping("/{messageId}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(
		    summary = "Đánh dấu tin nhắn đã đọc"
		)
    public ResponseEntity<Void> markAsRead(@PathVariable Integer messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
}
