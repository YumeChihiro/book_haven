package com.haven.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class ManagerFeedbackResponseDTO {
    private int feedbackId;
    private int customerId;
    private int managerId;
    private int orderId;
    private int rating;
    private String comment;
    private String replyText; // Từ FeedbackReply (lấy reply đầu tiên nếu có)
    private LocalDateTime repliedAt; // Từ FeedbackReply
    private String repliedBy; // Từ FeedbackReply
    private LocalDateTime createdAt;
    
    
	public ManagerFeedbackResponseDTO(int feedbackId, int customerId, int managerId, int orderId, int rating,
			String comment, LocalDateTime createdAt) {
		super();
		this.feedbackId = feedbackId;
		this.customerId = customerId;
		this.managerId = managerId;
		this.orderId = orderId;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
	}
	
	
	public ManagerFeedbackResponseDTO(int feedbackId, int customerId, int managerId, int orderId, int rating,
			String comment, String replyText, LocalDateTime repliedAt, String repliedBy, LocalDateTime createdAt) {
		super();
		this.feedbackId = feedbackId;
		this.customerId = customerId;
		this.managerId = managerId;
		this.orderId = orderId;
		this.rating = rating;
		this.comment = comment;
		this.replyText = replyText;
		this.repliedAt = repliedAt;
		this.repliedBy = repliedBy;
		this.createdAt = createdAt;
	}


	public int getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getManagerId() {
		return managerId;
	}
	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getReplyText() {
		return replyText;
	}
	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}
	public LocalDateTime getRepliedAt() {
		return repliedAt;
	}
	public void setRepliedAt(LocalDateTime repliedAt) {
		this.repliedAt = repliedAt;
	}
	public String getRepliedBy() {
		return repliedBy;
	}
	public void setRepliedBy(String repliedBy) {
		this.repliedBy = repliedBy;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}