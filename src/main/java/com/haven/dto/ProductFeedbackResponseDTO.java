package com.haven.dto;

import java.time.LocalDateTime;


public class ProductFeedbackResponseDTO {
    private int feedbackId;
    private int customerId;
    private int productId;
    private int orderId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    
    
	public ProductFeedbackResponseDTO(int feedbackId, int customerId, int productId, int orderId, int rating,
			String comment, LocalDateTime createdAt) {
		super();
		this.feedbackId = feedbackId;
		this.customerId = customerId;
		this.productId = productId;
		this.orderId = orderId;
		this.rating = rating;
		this.comment = comment;
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
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
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
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}