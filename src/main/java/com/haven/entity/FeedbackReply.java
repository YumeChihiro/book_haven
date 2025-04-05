package com.haven.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedback_reply")
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private int replyId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String replyText;

    @Column(nullable = false)
    private LocalDateTime repliedAt = LocalDateTime.now();

    @Column(nullable = false)
    private String repliedBy; // Shop hoặc Admin

    @ManyToOne
    @JoinColumn(name = "product_feedback_id")
    private ProductFeedback productFeedback;

    @ManyToOne
    @JoinColumn(name = "manager_feedback_id")
    private ManagerFeedback managerFeedback;

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
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

	public ProductFeedback getProductFeedback() {
		return productFeedback;
	}

	public void setProductFeedback(ProductFeedback productFeedback) {
		this.productFeedback = productFeedback;
	}

	public ManagerFeedback getManagerFeedback() {
		return managerFeedback;
	}

	public void setManagerFeedback(ManagerFeedback managerFeedback) {
		this.managerFeedback = managerFeedback;
	}
    
    
}
