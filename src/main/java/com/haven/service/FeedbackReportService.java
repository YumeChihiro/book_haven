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
public class FeedbackReportService {

    @Autowired
    private FeedbackReportRepository feedbackReportRepository;

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

    @Transactional
    public FeedbackReportResponseDTO createReport(FeedbackReportRequestDTO dto, Integer userId) {
        Customer customer = customerRepository.findByCustomerId(userId).orElse(null);
        Manager manager = managerRepository.findByManagerId(userId).orElse(null);

        if (customer == null && manager == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        FeedbackReport report = new FeedbackReport();
        report.setReportReason(dto.getReportReason());

        // Gán người báo cáo
        if (customer != null) {
            report.setReportedByCustomer(customer);
        } else {
            report.setReportedByManager(manager);
        }

        // Logic cho customer: Chỉ report FeedbackReply của shop gửi cho họ
        if (customer != null) {
            if (dto.getFeedbackReplyId() == null) {
                throw new RuntimeException("Customer must specify a feedback reply ID to report");
            }
            if (dto.getManagerFeedbackId() != null) {
                throw new RuntimeException("Customer cannot report manager feedback directly");
            }

            Optional<FeedbackReply> replyOpt = feedbackReplyRepository.findById(dto.getFeedbackReplyId());
            if (replyOpt.isEmpty()) {
                throw new RuntimeException("Feedback reply not found");
            }
            FeedbackReply reply = replyOpt.get();

            ManagerFeedback managerFeedback = reply.getManagerFeedback();
            ProductFeedback productFeedback = reply.getProductFeedback();

            if (managerFeedback != null && managerFeedback.getCustomer().getCustomerId() != customer.getCustomerId()) {
                throw new RuntimeException("You can only report replies to your own manager feedback");
            }
            if (productFeedback != null && productFeedback.getCustomer().getCustomerId() != customer.getCustomerId()) {
                throw new RuntimeException("You can only report replies to your own product feedback");
            }

            report.setManagerFeedback(managerFeedback);
            report.setProductFeedback(productFeedback);
        }

        // Logic cho manager: Report ManagerFeedback liên quan đến shop của họ
        if (manager != null) {
            if (dto.getFeedbackReplyId() != null) {
                throw new RuntimeException("Manager cannot report feedback replies");
            }
            if (dto.getManagerFeedbackId() == null) {
                throw new RuntimeException("Manager must specify a manager feedback ID to report");
            }

            Optional<ManagerFeedback> mfOpt = managerFeedbackRepository.findById(dto.getManagerFeedbackId());
            if (mfOpt.isEmpty()) {
                throw new RuntimeException("Manager feedback not found");
            }
            ManagerFeedback managerFeedback = mfOpt.get();

            if (managerFeedback.getManager().getManagerId() != manager.getManagerId()) {
                throw new RuntimeException("You can only report feedback related to your shop");
            }

            report.setManagerFeedback(managerFeedback);
        }

        // Kiểm tra xem đã report feedback này chưa
        boolean alreadyReported = feedbackReportRepository.findAll()
            .stream()
            .anyMatch(r -> 
                (customer != null && r.getReportedByCustomer() != null && r.getReportedByCustomer().getCustomerId() == customer.getCustomerId()) ||
                (manager != null && r.getReportedByManager() != null && r.getReportedByManager().getManagerId() == manager.getManagerId())
                && (
                    (dto.getFeedbackReplyId() != null && 
                        ((r.getManagerFeedback() != null && r.getManagerFeedback().getReplies().stream().anyMatch(reply -> reply.getReplyId() == dto.getFeedbackReplyId())) ||
                         (r.getProductFeedback() != null && r.getProductFeedback().getReplies().stream().anyMatch(reply -> reply.getReplyId() == dto.getFeedbackReplyId())))) ||
                    (dto.getManagerFeedbackId() != null && r.getManagerFeedback() != null && r.getManagerFeedback().getFeedbackId() == dto.getManagerFeedbackId())
                )
            );
        if (alreadyReported) {
            throw new RuntimeException("You have already reported this feedback or reply");
        }

        FeedbackReport savedReport = feedbackReportRepository.save(report);
        return convertToDTO(savedReport);
    }

    // Xem các report của người dùng hiện tại
    public List<FeedbackReportResponseDTO> getReportsByUser(Integer userId) {
        Customer customer = customerRepository.findByCustomerId(userId).orElse(null);
        Manager manager = managerRepository.findByManagerId(userId).orElse(null);

        if (customer == null && manager == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        List<FeedbackReport> reports = customer != null
            ? feedbackReportRepository.findAll().stream()
                .filter(r -> r.getReportedByCustomer() != null && r.getReportedByCustomer().getCustomerId() == customer.getCustomerId())
                .collect(Collectors.toList())
            : feedbackReportRepository.findAll().stream()
                .filter(r -> r.getReportedByManager() != null && r.getReportedByManager().getManagerId() == manager.getManagerId())
                .collect(Collectors.toList());

        return reports.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}