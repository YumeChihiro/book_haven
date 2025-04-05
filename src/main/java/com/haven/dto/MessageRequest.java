package com.haven.dto;


public class MessageRequest {
    private Integer customerId; // Dùng khi gửi từ Shop đến Customer
    private Integer managerId;  // Dùng khi gửi từ Admin đến Shop
    private String title;
    private String content;
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getManagerId() {
		return managerId;
	}
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

   
}
