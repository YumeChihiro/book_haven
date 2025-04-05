package com.haven.service;


import com.haven.dto.ManagerFeedbackResponseDTO;
import com.haven.dto.ManagerFeedbackReplyRequestDTO;
import com.haven.entity.*;
import com.haven.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopFeedbackService {

    @Autowired
    private ManagerFeedbackRepository managerFeedbackRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private FeedbackReplyRepository feedbackReplyRepository;

    private ManagerFeedbackResponseDTO convertToDTO(ManagerFeedback feedback) {
        FeedbackReply reply = feedback.getReplies().isEmpty() ? null : feedback.getReplies().get(0); // Lấy reply đầu tiên nếu có
        return new ManagerFeedbackResponseDTO(
            feedback.getFeedbackId(),
            feedback.getCustomer().getCustomerId(),
            feedback.getManager().getManagerId(),
            feedback.getOrder().getOrderId(),
            feedback.getRating(),
            feedback.getComment(),
            reply != null ? reply.getReplyText() : null,
            reply != null ? reply.getRepliedAt() : null,
            reply != null ? reply.getRepliedBy() : null,
            feedback.getCreatedAt()
        );
    }

    // View Feedback for Shop (cho manager)
    public List<ManagerFeedbackResponseDTO> getFeedbackForShop(Integer shopId) {
      
        Manager manager = managerRepository.findByManagerId(shopId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        List<ManagerFeedback> feedbacks = managerFeedbackRepository.findByManagerManagerId(manager.getManagerId());
        return feedbacks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Reply Feedback (cho manager)
    @Transactional
    public ManagerFeedbackResponseDTO replyFeedback(ManagerFeedbackReplyRequestDTO dto, Integer shopId) {
    	 Manager manager = managerRepository.findByManagerId(shopId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

        Optional<ManagerFeedback> feedbackOpt = managerFeedbackRepository.findById(dto.getFeedbackId());
        if (feedbackOpt.isEmpty()) {
            throw new RuntimeException("Feedback not found");
        }
        ManagerFeedback feedback = feedbackOpt.get();

        // Kiểm tra feedback thuộc về shop của manager
        if (!(feedback.getManager().getManagerId() == (manager.getManagerId()))) {
            throw new RuntimeException("You can only reply to feedback for your shop");
        }

        // Kiểm tra xem đã reply chưa
        if (!feedback.getReplies().isEmpty()) {
            throw new RuntimeException("This feedback has already been replied to");
        }

        // Tạo reply mới
        FeedbackReply reply = new FeedbackReply();
        reply.setReplyText(dto.getReply());
        reply.setRepliedBy(manager.getEmail()); // Hoặc manager.getManagerName() tùy yêu cầu
        reply.setManagerFeedback(feedback); // Liên kết với ManagerFeedback
        feedbackReplyRepository.save(reply);

        // Thêm reply vào danh sách replies của feedback
        feedback.getReplies().add(reply);

        return convertToDTO(feedback);
    }
}