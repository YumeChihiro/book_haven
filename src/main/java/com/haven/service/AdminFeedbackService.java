package com.haven.service;

import com.haven.dto.FeedbackReportRequestDTO;
import com.haven.dto.FeedbackReportResponseDTO;
import com.haven.entity.*;
import com.haven.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminFeedbackService {

    @Autowired
    private FeedbackReportRepository feedbackReportRepository;

    @Autowired
    private ProductFeedbackRepository productFeedbackRepository;

    @Autowired
    private ManagerFeedbackRepository managerFeedbackRepository;

    @Autowired
    private FeedbackReplyRepository feedbackReplyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private FeedbackReportResponseDTO convertToDTO(FeedbackReport report) {
        return new FeedbackReportResponseDTO(
            report.getReportId(),
            report.getReportReason(),
            report.getReportStatus().name(),
            report.getReportedAt(),
            report.getProductFeedback() != null ? report.getProductFeedback().getFeedbackId() : null,
            report.getManagerFeedback() != null ? report.getManagerFeedback().getFeedbackId() : null,
            report.getReportedByCustomer() != null ? report.getReportedByCustomer().getCustomerId() : null,
            report.getReportedByManager() != null ? report.getReportedByManager().getManagerId() : null
        );
    }

    // API mới cho Admin: Xem tất cả báo cáo
    public List<FeedbackReportResponseDTO> getAllReports() {    
       
        List<FeedbackReport> reports = feedbackReportRepository.findAll();
        return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // API mới cho Admin: Cập nhật trạng thái báo cáo
    @Transactional
    public FeedbackReportResponseDTO updateReportStatus(Integer reportId, String status) {

        FeedbackReport report = feedbackReportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        try {
            FeedbackReport.ReportStatus newStatus = FeedbackReport.ReportStatus.valueOf(status.toUpperCase());
            report.setReportStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value. Must be PENDING, REVIEWED, or RESOLVED");
        }

        FeedbackReport updatedReport = feedbackReportRepository.save(report);
        return convertToDTO(updatedReport);
    }

    // API mới cho Admin: Xóa báo cáo
    @Transactional
    public void deleteReport(Integer reportId) {
        FeedbackReport report = feedbackReportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        feedbackReportRepository.delete(report);
    }
}