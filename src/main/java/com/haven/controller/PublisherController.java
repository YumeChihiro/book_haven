package com.haven.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.PublisherDTO;
import com.haven.service.PublisherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/publisher")
@Tag(name = "Publisher api", description = "Crud publisher từ admin")
@SecurityRequirement(name = "JWT")
public class PublisherController {
	
	private final PublisherService publisherService;
	
	public PublisherController(PublisherService publisherService) {
		super();
		this.publisherService = publisherService;
	}

	@PostMapping("/publishers")
    @PreAuthorize("hasRole('ADMIN')")
	  @Operation(
			    summary = "Tạo mới nhà xuất bản"
			)
    public ResponseEntity<?> createPublisher(
            @Valid @RequestBody PublisherDTO publisherDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            PublisherDTO createdPublisher = publisherService.createPublisher(publisherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPublisher);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo nhà xuất bản: " + e.getMessage());
        }
    }

    @GetMapping("/publishers/{publisherId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xem chi tiết nhà xuất bản"
		)
    public ResponseEntity<?> getPublisherById(@PathVariable Integer publisherId) {
        try {
            PublisherDTO publisher = publisherService.getPublisherById(publisherId);
            return ResponseEntity.ok(publisher);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy nhà xuất bản: " + e.getMessage());
        }
    }

    @GetMapping("/publishers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xem tất cả nhà xuất bản"
		)
    public ResponseEntity<?> getAllPublishers() {
        try {
            List<PublisherDTO> publishers = publisherService.getAllPublishers();
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách nhà xuất bản: " + e.getMessage());
        }
    }

    @PutMapping("/publishers/{publisherId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Cập nhật nhà xuất bản"
		)
    public ResponseEntity<?> updatePublisher(
            @PathVariable Integer publisherId,
            @Valid @RequestBody PublisherDTO publisherDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            PublisherDTO updatedPublisher = publisherService.updatePublisher(publisherId, publisherDTO);
            return ResponseEntity.ok(updatedPublisher);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật nhà xuất bản: " + e.getMessage());
        }
    }

    @DeleteMapping("/publishers/{publisherId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xóa nhà xuất bản"
		)
    public ResponseEntity<?> deletePublisher(@PathVariable Integer publisherId) {
        try {
            publisherService.deletePublisher(publisherId);
            return ResponseEntity.ok("Nhà xuất bản đã bị xóa thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa nhà xuất bản: " + e.getMessage());
        }
    }
    
    
}
