package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.FeedbackReportRequestDTO;
import com.haven.dto.FeedbackReportResponseDTO;
import com.haven.dto.ManagerFeedbackReplyRequestDTO;
import com.haven.dto.ManagerFeedbackResponseDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.FeedbackReportService;
import com.haven.service.ShopFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/shop/feedback")
@Tag(name = "Feedback, report shop api", description = "Xem và repyly feedback, tạp report cho shop")
public class ShopFeedbackController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
    private ShopFeedbackService managerFeedbackService;
	
	@Autowired
	private FeedbackReportService feedbackReportService;

    // View Feedback for Shop (chỉ manager)
    @GetMapping
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Xem đánh giá cho shop"
		)
    public ResponseEntity<List<ManagerFeedbackResponseDTO>> getFeedbackForShop( @RequestHeader("Authorization") String token) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        List<ManagerFeedbackResponseDTO> feedbacks = managerFeedbackService.getFeedbackForShop(userId);
        return ResponseEntity.ok(feedbacks);
    }

    // Reply Feedback (chỉ manager)
    @PostMapping("/reply")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Trả lời đánh giá"
		)
    public ResponseEntity<ManagerFeedbackResponseDTO> replyFeedback(
    		@RequestBody ManagerFeedbackReplyRequestDTO dto,
    		 @RequestHeader("Authorization") String token) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        ManagerFeedbackResponseDTO response = managerFeedbackService.replyFeedback(dto,userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/create-report")
    @PreAuthorize("hasAnyRole('SHOP')")
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

    // View Reports by Reporter (truyền interfaceId qua path variable)
    @GetMapping("/my-reports")
    @PreAuthorize("hasAnyRole('SHOP')")
    @Operation(
		    summary = "Xem báo cáo đã tạo"
		)
    public ResponseEntity<List<FeedbackReportResponseDTO>> getReportsByReporter(
    		 @RequestHeader("Authorization") String token) {
    	
    	String jwt = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(jwt);
        List<FeedbackReportResponseDTO> reports = feedbackReportService.getReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }
}
