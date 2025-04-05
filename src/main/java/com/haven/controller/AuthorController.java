package com.haven.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.haven.dto.AuthorDTO;
import com.haven.service.AuthorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shop/authors")
@Tag(name = "Author api", description = "Crud tác giả từ shop")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // Create
    @PostMapping
    @PreAuthorize("hasRole('SHOP')") // Chỉ Manager (role SHOP) có quyền
    @Operation(
		    summary = "Tạo mới tác giả"
		)
    public ResponseEntity<?> createAuthor(
            @Valid @RequestBody AuthorDTO authorDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            AuthorDTO createdAuthor = authorService.createAuthor(authorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo tác giả: " + e.getMessage());
        }
    }

    // Read (Get by ID)
    @GetMapping("/{authorId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Xem thông tin một tác giả của shop"
		)
    public ResponseEntity<?> getAuthorById(@PathVariable Integer authorId) {
        try {
            AuthorDTO author = authorService.getAuthorById(authorId);
            return ResponseEntity.ok(author);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy tác giả: " + e.getMessage());
        }
    }

    // Read (Get all)
    @GetMapping
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Xem tất cả tác giả mà shop đã tạo"
		)
    public ResponseEntity<?> getAllAuthors() {
        try {
            List<AuthorDTO> authors = authorService.getAllAuthors();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách tác giả: " + e.getMessage());
        }
    }

    // Update
    @PutMapping("/{authorId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Cập nhật thông tin tác giả"
		)
    public ResponseEntity<?> updateAuthor(
            @PathVariable Integer authorId,
            @Valid @RequestBody AuthorDTO authorDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            AuthorDTO updatedAuthor = authorService.updateAuthor(authorId, authorDTO);
            return ResponseEntity.ok(updatedAuthor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật tác giả: " + e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{authorId}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
		    summary = "Xóa tác giả"
		)
    public ResponseEntity<?> deleteAuthor(@PathVariable Integer authorId) {
        try {
            authorService.deleteAuthor(authorId);
            return ResponseEntity.ok("Tác giả đã bị xóa thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa tác giả: " + e.getMessage());
        }
    }
}