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

import com.haven.dto.CategoryDTO;
import com.haven.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/caterory")
@Tag(name = "Category api", description = "Crud loại của sản phẩm từ admin")
public class CategoryController {
	
	private final CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}

	@PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
	  @Operation(
			    summary = "Thêm loại mới"
			)
    public ResponseEntity<?> createCategory(
            @RequestBody CategoryDTO categoryDTO) {
     
        try {
            CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo danh mục: " + e.getMessage());
        }
    }

    @GetMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xem thông tin của một loại nhất định"
		)
    public ResponseEntity<?> getCategoryById(@PathVariable Integer categoryId) {
        try {
            CategoryDTO category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh mục: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xem tất cả các loại"
		)
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách danh mục: " + e.getMessage());
        }
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Cập nhật loại"
		)
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xóa loại"
		)
    public ResponseEntity<?> deleteCategory(@PathVariable Integer categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok("Danh mục đã bị xóa thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa danh mục: " + e.getMessage());
        }
    }
}
