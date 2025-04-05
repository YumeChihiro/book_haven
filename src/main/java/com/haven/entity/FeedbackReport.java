package com.haven.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int reportId;

    @Column(name = "report_reason", nullable = false, columnDefinition = "TEXT")
    private String reportReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ReportStatus reportStatus = ReportStatus.PENDING;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_feedback_id", nullable = true)
    private ProductFeedback productFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_feedback_id", nullable = true)
    private ManagerFeedback managerFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_customer_id", nullable = true)
    private Customer reportedByCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_manager_id", nullable = true)
    private Manager reportedByManager;

    public enum ReportStatus {
        PENDING, REVIEWED, RESOLVED
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

	public ReportStatus getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(ReportStatus reportStatus) {
		this.reportStatus = reportStatus;
	}

	public LocalDateTime getReportedAt() {
		return reportedAt;
	}

	public void setReportedAt(LocalDateTime reportedAt) {
		this.reportedAt = reportedAt;
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

	public Customer getReportedByCustomer() {
		return reportedByCustomer;
	}

	public void setReportedByCustomer(Customer reportedByCustomer) {
		this.reportedByCustomer = reportedByCustomer;
	}

	public Manager getReportedByManager() {
		return reportedByManager;
	}

	public void setReportedByManager(Manager reportedByManager) {
		this.reportedByManager = reportedByManager;
	}
    
    
}