package com.haven.websocket;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Lấy lịch sử tin nhắn giữa customer và manager
    List<ChatMessage> findByCustomerCustomerIdAndManagerManagerIdOrderByTimestampAsc(Integer customerId, Integer managerId);
}