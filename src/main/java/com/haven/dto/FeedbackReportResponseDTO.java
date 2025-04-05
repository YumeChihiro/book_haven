package com.haven.dto;

import java.time.LocalDateTime;

public class FeedbackReportResponseDTO {
    private int reportId;
    private String reportReason;
    private String reportStatus;
    private LocalDateTime reportedAt;
    private Integer productFeedbackId;
    private Integer managerFeedbackId;
    private Integer reportedByCustomerId; // Thêm để trả về customer_id
    private Integer reportedByManagerId;
    
    
	public FeedbackReportResponseDTO(int reportId, String reportReason, String reportStatus, LocalDateTime reportedAt,
			Integer productFeedbackId, Integer managerFeedbackId, Integer reportedByCustomerId,
			Integer reportedByManagerId) {
		super();
		this.reportId = reportId;
		this.reportReason = reportReason;
		this.reportStatus = reportStatus;
		this.reportedAt = reportedAt;
		this.productFeedbackId = productFeedbackId;
		this.managerFeedbackId = managerFeedbackId;
		this.reportedByCustomerId = reportedByCustomerId;
		this.reportedByManagerId = reportedByManagerId;
	}
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	public String getReportReason() {
		return reportReason;
	}
	public void setReportReason(String reportReason) {
		this.reportReason = reportReason;
	}
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public LocalDateTime getReportedAt() {
		return reportedAt;
	}
	public void setReportedAt(LocalDateTime reportedAt) {
		this.reportedAt = reportedAt;
	}
	public Integer getProductFeedbackId() {
		return productFeedbackId;
	}
	public void setProductFeedbackId(Integer productFeedbackId) {
		this.productFeedbackId = productFeedbackId;
	}
	public Integer getManagerFeedbackId() {
		return managerFeedbackId;
	}
	public void setManagerFeedbackId(Integer managerFeedbackId) {
		this.managerFeedbackId = managerFeedbackId;
	}
	public Integer getReportedByCustomerId() {
		return reportedByCustomerId;
	}
	public void setReportedByCustomerId(Integer reportedByCustomerId) {
		this.reportedByCustomerId = reportedByCustomerId;
	}
	public Integer getReportedByManagerId() {
		return reportedByManagerId;
	}
	public void setReportedByManagerId(Integer reportedByManagerId) {
		this.reportedByManagerId = reportedByManagerId;
	}
    
    
    
	
}