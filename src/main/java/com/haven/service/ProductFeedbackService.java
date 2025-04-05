package com.haven.service;

import com.haven.dto.ProductFeedbackRequestDTO;
import com.haven.dto.ProductFeedbackResponseDTO;
import com.haven.entity.Customer;
import com.haven.entity.Order;
import com.haven.entity.Product;
import com.haven.entity.ProductFeedback;
import com.haven.repository.CustomerRepository;
import com.haven.repository.OrderRepository;
import com.haven.repository.ProductFeedbackRepository;
import com.haven.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductFeedbackService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductFeedbackService.class);
	

    @Autowired
    private ProductFeedbackRepository productFeedbackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private ProductFeedbackResponseDTO convertToDTO(ProductFeedback feedback) {
        return new ProductFeedbackResponseDTO(
            feedback.getFeedbackId(),
            feedback.getCustomer().getCustomerId(),
            feedback.getProduct().getProductId(),
            feedback.getOrder().getOrderId(),
            feedback.getRating(),
            feedback.getComment(),
            feedback.getCreatedAt()
        );
    }

    // Create Feedback
    @Transactional
    public ProductFeedbackResponseDTO createFeedback(ProductFeedbackRequestDTO dto, Integer customerId) {
    	
    	  logger.info("Id customer Feedback: " + customerId);
      
    	Customer customer = customerRepository.findByCustomerId(customerId)
    	        .orElseThrow(() -> new RuntimeException("Customer not found"));
    	
        // Kiểm tra order
        Optional<Order> orderOpt = orderRepository.findById(dto.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderOpt.get();

        // Kiểm tra order thuộc về customer và đã hoàn thành
        if (!(order.getCustomer().getCustomerId() == customer.getCustomerId())) {
            throw new RuntimeException("Order does not belong to this customer");
        }
        if (!"Delivered".equalsIgnoreCase(order.getOrderStatus().getName())) {
            throw new RuntimeException("Feedback can only be submitted for completed orders");
        }

        // Kiểm tra product
        Optional<Product> productOpt = productRepository.findById(dto.getProductId());
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = productOpt.get();

        // Kiểm tra rating hợp lệ
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Kiểm tra xem customer đã feedback cho product trong order này chưa
        boolean alreadyFeedback = productFeedbackRepository.findByProductProductId(dto.getProductId())
            .stream()
            .anyMatch(f -> f.getOrder().getOrderId() == dto.getOrderId() 
                && f.getCustomer().getCustomerId() == customer.getCustomerId());
        if (alreadyFeedback) {
            throw new RuntimeException("You have already submitted feedback for this product in this order");
        }

        // Tạo feedback
        ProductFeedback feedback = new ProductFeedback();
        feedback.setCustomer(customer);
        feedback.setProduct(product);
        feedback.setOrder(order);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());

        ProductFeedback savedFeedback = productFeedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    // View Feedback by Product ID
    public List<ProductFeedbackResponseDTO> getFeedbackByProductId(int productId) {
        List<ProductFeedback> feedbacks = productFeedbackRepository.findByProductProductId(productId);
        return feedbacks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}