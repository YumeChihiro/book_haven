package com.haven.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.ManagerUpdateDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.ManagerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shop")
@Tag(name = "Shop Update API", description = "Update thông tin tài khoản của shop")
public class ShopController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
    private ManagerService managerService;
	
    @PutMapping("/{managerId}")
    @PreAuthorize("hasRole('SHOP')")
	@Operation(
		    summary = "Update thông tin tài khoản shop (ngoại trừ password và email)"
		)
    public ResponseEntity<?> updateManager(
    		 @RequestHeader("Authorization") String token,
            @Valid @RequestBody ManagerUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> 
                    errors.put(error.getField(), error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(errors);
            }
            
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);

            ManagerUpdateDTO responseDTO = managerService.updateManager(userId, updateDTO, userDetails);
            return ResponseEntity.ok(responseDTO);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền cập nhật thông tin này");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy manager");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật thông tin: " + e.getMessage());
        }
    }
    
    
}
