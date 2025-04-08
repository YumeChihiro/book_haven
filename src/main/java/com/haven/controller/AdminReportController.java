package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.FeedbackReportResponseDTO;
import com.haven.service.AdminFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/report")
@Tag(name = "Admin report feedback api", description = "Xem, cập nhật, xóa báo cáo feedback từ shop hoặc customer")
public class AdminReportController {

	@Autowired
	private AdminFeedbackService adminFeedbackService;
	
	@GetMapping("/all-reports")
	 @Operation(
			    summary = "Xem tất cả report"
			)
    @PreAuthorize("hasRole('ADMIN')") // Chỉ amdin được phép
    public ResponseEntity<List<FeedbackReportResponseDTO>> getAllReports() {
      
        List<FeedbackReportResponseDTO> reports = adminFeedbackService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    // Admin cập nhật trạng thái báo cáo
    @PutMapping("/update-status/{reportId}")
    @Operation(
		    summary = "Cập nhật trạng thái report"
		)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackReportResponseDTO> updateReportStatus(
            @PathVariable("reportId") Integer reportId,
            @RequestParam("status") String status) {
      
        FeedbackReportResponseDTO response = adminFeedbackService.updateReportStatus(reportId, status);
        return ResponseEntity.ok(response);
    }

    // Admin xóa báo cáo
    @DeleteMapping("/delete/{reportId}")
    @Operation(
		    summary = "Xóa report"
		)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReport(
            @PathVariable("reportId") Integer reportId) {
      
    	adminFeedbackService.deleteReport(reportId);
        return ResponseEntity.ok("Report deleted successfully");
    }
}
