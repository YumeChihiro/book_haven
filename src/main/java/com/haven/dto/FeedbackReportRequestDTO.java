package com.haven.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor

public class FeedbackReportRequestDTO {
    private Integer feedbackReplyId; // Dành cho customer
    private Integer managerFeedbackId; // Dành cho manager
    private String reportReason;
	public Integer getFeedbackReplyId() {
		return feedbackReplyId;
	}
	public void setFeedbackReplyId(Integer feedbackReplyId) {
		this.feedbackReplyId = feedbackReplyId;
	}
	public Integer getManagerFeedbackId() {
		return managerFeedbackId;
	}
	public void setManagerFeedbackId(Integer managerFeedbackId) {
		this.managerFeedbackId = managerFeedbackId;
	}
	public String getReportReason() {
		return reportReason;
	}
	public void setReportReason(String reportReason) {
		this.reportReason = reportReason;
	}
    
    
}