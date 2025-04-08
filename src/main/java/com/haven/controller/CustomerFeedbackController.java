package com.haven.controller;

import com.haven.dto.FeedbackReportRequestDTO;
import com.haven.dto.FeedbackReportResponseDTO;
import com.haven.dto.ManagerFeedbackRequestDTO;
import com.haven.dto.ManagerFeedbackResponseDTO;
import com.haven.dto.ProductFeedbackRequestDTO;
import com.haven.dto.ProductFeedbackResponseDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.FeedbackReportService;
import com.haven.service.ManagerFeedbackService;
import com.haven.service.ProductFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/feedback")
@Tag(name = "Feedback, report customer api", description = "Xem, tạo feedback và report cho customer")
public class CustomerFeedbackController {
	
	@Autowired
	private JwtUtil jwtUtil;

    @Autowired
    private ProductFeedbackService productFeedbackService;
    
    @Autowired
    private ManagerFeedbackService managerFeedbackService;
    
    @Autowired
    private FeedbackReportService feedbackReportService;

    // Create Feedback (chỉ customer đăng nhập mới được tạo)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
		    summary = "Tạo đánh giá sản phẩm"
		)
    public ResponseEntity<ProductFeedbackResponseDTO> createFeedback(
    		@RequestBody ProductFeedbackRequestDTO dto,
    		 @RequestHeader("Authorization") String token) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        ProductFeedbackResponseDTO response = productFeedbackService.createFeedback(dto,userId);
        return ResponseEntity.ok(response);
    }

    // View Feedback by Product ID (ai cũng có thể xem)
    @GetMapping("/product/{productId}")
    @Operation(
		    summary = "Xem feedback của product"
		)
    public ResponseEntity<List<ProductFeedbackResponseDTO>> getFeedbackByProductId(@PathVariable("productId") int productId) {
        List<ProductFeedbackResponseDTO> feedbacks = productFeedbackService.getFeedbackByProductId(productId);
        return ResponseEntity.ok(feedbacks);
    }
    
    @PostMapping("/create-feed-shop")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
		    summary = "Tạo đánh giá shop"
		)
    public ResponseEntity<ManagerFeedbackResponseDTO> createFeedback(
    		@RequestBody ManagerFeedbackRequestDTO dto,
    		 @RequestHeader("Authorization") String token) {
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        ManagerFeedbackResponseDTO response = managerFeedbackService.createFeedback(dto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-feedback")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
		    summary = "Xem đánh giá và customer cụ thể đã tạo"
		)
    public ResponseEntity<List<ManagerFeedbackResponseDTO>> getFeedbackByCustomer( @RequestHeader("Authorization") String token) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        List<ManagerFeedbackResponseDTO> feedbacks = managerFeedbackService.getFeedbackByCustomer(userId);
        return ResponseEntity.ok(feedbacks);
    }

    // View All Feedback by Manager ID (tất cả feedback của shop, chỉ customer xem)
    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
		    summary = "Xem tất cả đánh giá cho shop"
		)
    public ResponseEntity<List<ManagerFeedbackResponseDTO>> getAllFeedbackByManagerId(@PathVariable("managerId") Integer managerId) {
        List<ManagerFeedbackResponseDTO> feedbacks = managerFeedbackService.getAllFeedbackByManagerId(managerId);
        return ResponseEntity.ok(feedbacks);
    }
    
    @PostMapping("/create-report")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(
		    summary = "Tạo báo cáo"
		)
    public ResponseEntity<FeedbackReportResponseDTO> createReport(
    		 @RequestHeader("Authorization") String token,
            @RequestBody FeedbackReportRequestDTO dto) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        FeedbackReportResponseDTO response = feedbackReportService.createReport(dto, userId);
        return ResponseEntity.ok(response);
    }

  
    @GetMapping("/my-reports")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(
		    summary = "Xem báo cáo mà customer đã tạo"
		)
    public ResponseEntity<List<FeedbackReportResponseDTO>> getReportsByReporter(
    		 @RequestHeader("Authorization") String token) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        List<FeedbackReportResponseDTO> reports = feedbackReportService.getReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }
}