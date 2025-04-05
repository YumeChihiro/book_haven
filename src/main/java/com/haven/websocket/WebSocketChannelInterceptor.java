package com.haven.websocket;

import com.haven.securities.JwtUtil;

import io.jsonwebtoken.MalformedJwtException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // Đảm bảo đã khai báo bean này

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);

    public WebSocketChannelInterceptor(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        logger.info("WebSocketChannelInterceptor đã được khởi tạo");
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                logger.info("CONNECT token: " + token);
                String username = jwtUtil.extractUsername(token);
                // Sử dụng UserDetailsService để load thông tin cho user (hoặc tự tạo đối tượng)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (!jwtUtil.validateToken(token, userDetails)) {
                    throw new SecurityException("Invalid or expired token");
                }
                // Tạo và gán Authentication vào SecurityContextHolder
                Authentication authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Lưu thêm thông tin nếu cần
                accessor.getSessionAttributes().put("jwtToken", token);
                accessor.getSessionAttributes().put("userId", jwtUtil.extractUserId(token));
            } else {
                throw new SecurityException("No token provided");
            }
        }
        
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            // Ưu tiên lấy token từ session attribute lưu ở CONNECT
        	String token = (String) accessor.getSessionAttributes().get("jwtToken");
            if (token == null) {
                // Nếu không có, dự phòng lấy từ header
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new SecurityException("Authorization header is missing or invalid in SEND message.");
                }
                token = authHeader.substring(7);
            }
            logger.info("SEND token: " + token);
            long periodCount = token.chars().filter(ch -> ch == '.').count();
            if (periodCount != 2) {
                throw new MalformedJwtException("JWT string must contain exactly 2 period characters. Found: " + periodCount);
            }
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.validateToken(token, userDetails)) {
                throw new SecurityException("Invalid or expired token in SEND message.");
            }
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return message;
    }
}
