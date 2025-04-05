package com.haven.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.haven.dto.AddressDTO;
import com.haven.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.haven.securities.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
@SecurityRequirement(name = "JWT")
@Tag(name = "Address api", description = "Crud địa chỉ của customer và shop (lấy id tài khoản từ token để tạo)")
public class AddressController {

    private final AddressService addressService;
    private final JwtUtil jwtUtil;

    public AddressController(AddressService addressService, JwtUtil jwtUtil) {
        this.addressService = addressService;
        this.jwtUtil = jwtUtil;
    }

    // Create
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Cho phép cả Manager và Customer đã xác thực
    @Operation(
		    summary = "Tạo mới địa chỉ"
		)
    public ResponseEntity<?> createAddress(
            @Valid @RequestBody AddressDTO addressDTO,
            @RequestHeader("Authorization") String token,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);
            String roles = jwtUtil.getRolesFromToken(jwt); // Lấy "roles" từ token
            boolean isManager = !roles.equals("NO_ROLE"); // Manager nếu role không phải "NO_ROLE"
            AddressDTO createdAddress = addressService.createAddress(addressDTO, userId, isManager);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo địa chỉ: " + e.getMessage());
        }
    }

    // Read (Get by ID)
    @GetMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
		    summary = "Xem thông tin địa chỉ"
		)
    public ResponseEntity<?> getAddressById(
            @PathVariable Integer addressId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);
            String roles = jwtUtil.getRolesFromToken(jwt);
            boolean isManager = !roles.equals("NO_ROLE");
            AddressDTO address = addressService.getAddressById(addressId, userId, isManager);
            return ResponseEntity.ok(address);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy địa chỉ: " + e.getMessage());
        }
    }

    // Read (Get all by user)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
		    summary = "Xem tất cả thông tin địa chỉ"
		)
    public ResponseEntity<?> getAllAddresses(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);
            String roles = jwtUtil.getRolesFromToken(jwt);
            boolean isManager = !roles.equals("NO_ROLE");
            List<AddressDTO> addresses = addressService.getAllAddressesByUser(userId, isManager);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách địa chỉ: " + e.getMessage());
        }
    }

    // Update
    @PutMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
		    summary = "Cập nhật địa chỉ"
		)
    public ResponseEntity<?> updateAddress(
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressDTO addressDTO,
            @RequestHeader("Authorization") String token,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);
            String roles = jwtUtil.getRolesFromToken(jwt);
            boolean isManager = !roles.equals("NO_ROLE");
            AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO, userId, isManager);
            return ResponseEntity.ok(updatedAddress);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật địa chỉ: " + e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
		    summary = "Xóa địa chỉ"
		)
    public ResponseEntity<?> deleteAddress(
            @PathVariable Integer addressId,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtUtil.extractUserId(jwt);
            String roles = jwtUtil.getRolesFromToken(jwt);
            boolean isManager = !roles.equals("NO_ROLE");
            addressService.deleteAddress(addressId, userId, isManager);
            return ResponseEntity.ok("Địa chỉ đã bị xóa thành công.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa địa chỉ: " + e.getMessage());
        }
    }
}