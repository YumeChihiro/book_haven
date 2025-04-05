package com.haven.websocket;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;
    private Integer customerId;
    private Integer managerId;
    private String content;
    private String senderType; // "CUSTOMER" hoặc "MANAGER"
    private LocalDateTime timestamp;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}