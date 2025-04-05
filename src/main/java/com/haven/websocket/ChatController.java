package com.haven.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	


    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Xử lý tin nhắn từ client qua WebSocket
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessageDTO messageDTO, SimpMessageHeaderAccessor headerAccessor) {
        // Lưu tin nhắn vào database
    	 String token = headerAccessor.getFirstNativeHeader("Authorization");
    	if (token == null) {
    	    throw new SecurityException("JWT token not found in session");
    	}
    	
        ChatMessageDTO savedMessage = chatService.saveMessage(messageDTO, token);

        // Gửi tin nhắn đến cả customer và manager qua topic riêng
        String topic = "/topic/messages/" + messageDTO.getCustomerId() + "/" + messageDTO.getManagerId();
        messagingTemplate.convertAndSend(topic, savedMessage);
    }

    // Lấy lịch sử tin nhắn (bảo mật bằng Spring Security)
    @GetMapping("/history")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SHOP')")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @RequestParam Integer customerId,
            @RequestParam Integer managerId,
            @RequestHeader("Authorization") String token) {

        List<ChatMessageDTO> messages = chatService.getChatHistory(customerId, managerId, token);
        return ResponseEntity.ok(messages);
    }
}