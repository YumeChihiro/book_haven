package com.haven.service;

import com.haven.dto.ManagerFeedbackRequestDTO;
import com.haven.dto.ManagerFeedbackResponseDTO;
import com.haven.entity.*;
import com.haven.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagerFeedbackService {

    @Autowired
    private ManagerFeedbackRepository managerFeedbackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private ManagerFeedbackResponseDTO convertToDTO(ManagerFeedback feedback) {
        return new ManagerFeedbackResponseDTO(
            feedback.getFeedbackId(),
            feedback.getCustomer().getCustomerId(),
            feedback.getManager().getManagerId(),
            feedback.getOrder().getOrderId(),
            feedback.getRating(),
            feedback.getComment(),
            feedback.getCreatedAt()
        );
    }

    // Create Feedback
    @Transactional
    public ManagerFeedbackResponseDTO createFeedback(ManagerFeedbackRequestDTO dto, Integer cusId) {
        
        Customer customer = customerRepository.findByCustomerId(cusId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Kiểm tra order
        Optional<Order> orderOpt = orderRepository.findById(dto.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderOpt.get();

        // Kiểm tra order thuộc về customer
        if (!(order.getCustomer().getCustomerId() == (customer.getCustomerId()))) {
            throw new RuntimeException("Order does not belong to this customer");
        }

        // Kiểm tra manager
        Optional<Manager> managerOpt = managerRepository.findById(dto.getManagerId());
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("Manager not found");
        }
        Manager manager = managerOpt.get();

        // Kiểm tra customer đã mua hàng từ shop của manager
        List<Shop_Product> shopProducts = shopProductRepository.findByManagerManagerId(manager.getManagerId());
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(dto.getOrderId());
        boolean hasPurchasedFromShop = orderDetails.stream()
            .anyMatch(detail -> shopProducts.stream()
                .anyMatch(sp -> sp.getProduct().getProductId() == detail.getProduct().getProductId()));
        if (!hasPurchasedFromShop) {
            throw new RuntimeException("Customer has not purchased from this shop");
        }

        // Kiểm tra rating hợp lệ
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Kiểm tra xem customer đã feedback cho manager trong order này chưa
        boolean alreadyFeedback = managerFeedbackRepository.findByManagerManagerId(dto.getManagerId())
            .stream()
            .anyMatch(f -> f.getOrder().getOrderId() == dto.getOrderId() 
                && f.getCustomer().getCustomerId() == customer.getCustomerId());
        if (alreadyFeedback) {
            throw new RuntimeException("You have already submitted feedback for this shop in this order");
        }

        // Tạo feedback
        ManagerFeedback feedback = new ManagerFeedback();
        feedback.setCustomer(customer);
        feedback.setManager(manager);
        feedback.setOrder(order);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());

        ManagerFeedback savedFeedback = managerFeedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    public List<ManagerFeedbackResponseDTO> getFeedbackByCustomer(Integer cusId) {
       
        Customer customer = customerRepository.findByCustomerId(cusId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<ManagerFeedback> feedbacks = managerFeedbackRepository.findAll()
            .stream()
            .filter(f -> f.getCustomer().getCustomerId() == (customer.getCustomerId()))
            .collect(Collectors.toList());
        return feedbacks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // View All Feedback by Manager ID (tất cả feedback của shop)
    public List<ManagerFeedbackResponseDTO> getAllFeedbackByManagerId(Integer managerId) {
        List<ManagerFeedback> feedbacks = managerFeedbackRepository.findByManagerManagerId(managerId);
        return feedbacks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}