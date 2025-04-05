package com.haven.websocket;

import com.haven.entity.Customer;
import com.haven.entity.Manager;
import com.haven.repository.CustomerRepository;
import com.haven.repository.ManagerRepository;
import com.haven.securities.CustomerUserDetailService;
import com.haven.securities.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
	
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;
    
    @Autowired
    private CustomerUserDetailService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO, String jwtToken) {
        // Kiểm tra token có bị trống hay không
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new SecurityException("Missing JWT token");
        }
       
        logger.info("Service Save Message: " + jwtToken);
        logger.info("Period count: " + jwtToken.chars().filter(ch -> ch == '.').count());
        jwtToken = jwtToken.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(jwtToken, userDetails)) {
            throw new SecurityException("Invalid or expired token");
        }

        Integer userId = jwtUtil.extractUserId(jwtToken);
        String roles = jwtUtil.getRolesFromToken(jwtToken);
        if (userId == null || roles == null) {
            throw new SecurityException("Invalid token data");
        }

        // Kiểm tra phân quyền dựa trên thông tin từ JWT
        if (roles.contains("ROLE_CUSTOMER")) {
            if (!userId.equals(messageDTO.getCustomerId())) {
                throw new SecurityException("Customer not authorized to send this message");
            }
            messageDTO.setSenderType("CUSTOMER");
        } else if (roles.contains("ROLE_SHOP")) {
            if (!userId.equals(messageDTO.getManagerId())) {
                throw new SecurityException("Shop not authorized to send this message");
            }
            messageDTO.setSenderType("MANAGER");
        } else {
            throw new SecurityException("User does not have the required role");
        }

        Customer customer = customerRepository.findById(messageDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy customer với ID: " + messageDTO.getCustomerId()));
        Manager manager = managerRepository.findById(messageDTO.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy manager với ID: " + messageDTO.getManagerId()));

        ChatMessage message = new ChatMessage();
        message.setCustomer(customer);
        message.setManager(manager);
        message.setContent(messageDTO.getContent());
        message.setSenderType(messageDTO.getSenderType());
        message.setTimestamp(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageRepository.save(message);

        messageDTO.setId(savedMessage.getId());
        messageDTO.setTimestamp(savedMessage.getTimestamp());
        return messageDTO;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatHistory(Integer customerId, Integer managerId, String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new SecurityException("Missing JWT token");
        }
        jwtToken = jwtToken.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(jwtToken, userDetails)) {
            throw new SecurityException("Invalid or expired token");
        }
        Integer userId = jwtUtil.extractUserId(jwtToken);
        String roles = jwtUtil.getRolesFromToken(jwtToken);
        if (userId == null || roles == null) {
            throw new SecurityException("Invalid token data");
        }

        // Kiểm tra quyền truy cập chat
        if (roles.contains("ROLE_CUSTOMER")) {
            if (!userId.equals(customerId)) {
                throw new SecurityException("Customer not authorized to access this chat");
            }
        } else if (roles.contains("ROLE_SHOP")) {
            if (!userId.equals(managerId)) {
                throw new SecurityException("Shop not authorized to access this chat");
            }
        } else {
            throw new SecurityException("User does not have the required role");
        }

        List<ChatMessage> messages = chatMessageRepository.findByCustomerCustomerIdAndManagerManagerIdOrderByTimestampAsc(customerId, managerId);
        return messages.stream().map(message -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setId(message.getId());
            dto.setCustomerId(message.getCustomer().getCustomerId());
            dto.setManagerId(message.getManager().getManagerId());
            dto.setContent(message.getContent());
            dto.setSenderType(message.getSenderType());
            dto.setTimestamp(message.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}
