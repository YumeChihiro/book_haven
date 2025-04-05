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

import com.haven.dto.ShipperDTO;
import com.haven.service.ShipperService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/shipper")
@Tag(name = "Shipper api", description = "Crud shipper")
@SecurityRequirement(name = "JWT")
public class ShipperController {
	
	private ShipperService shipperService;

	public ShipperController(ShipperService shipperService) {
		super();
		this.shipperService = shipperService;
	}
	
	@PostMapping("/shippers")
    @PreAuthorize("hasRole('ADMIN')")
	 @Operation(
			    summary = "Tạo mới shipper"
			)
    public ResponseEntity<?> createShipper(
            @Valid @RequestBody ShipperDTO shipperDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            ShipperDTO createdShipper = shipperService.createShipper(shipperDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdShipper);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo shipper: " + e.getMessage());
        }
    }

    @GetMapping("/shippers/{shipperId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xem chi tiết shipper"
		)
    public ResponseEntity<?> getShipperById(@PathVariable Integer shipperId) {
        try {
            ShipperDTO shipper = shipperService.getShipperById(shipperId);
            return ResponseEntity.ok(shipper);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy shipper: " + e.getMessage());
        }
    }

    @GetMapping("/shippers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Lấy danh sách shipper"
		)
    public ResponseEntity<?> getAllShippers() {
        try {
            List<ShipperDTO> shippers = shipperService.getAllShippers();
            return ResponseEntity.ok(shippers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách shipper: " + e.getMessage());
        }
    }

    @PutMapping("/shippers/{shipperId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Cập nhật shipper"
		)
    public ResponseEntity<?> updateShipper(
            @PathVariable Integer shipperId,
            @Valid @RequestBody ShipperDTO shipperDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            ShipperDTO updatedShipper = shipperService.updateShipper(shipperId, shipperDTO);
            return ResponseEntity.ok(updatedShipper);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật shipper: " + e.getMessage());
        }
    }

    @DeleteMapping("/shippers/{shipperId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
		    summary = "Xóa shipper"
		)
    public ResponseEntity<?> deleteShipper(@PathVariable Integer shipperId) {
        try {
            shipperService.deleteShipper(shipperId);
            return ResponseEntity.ok("Shipper đã bị xóa thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa shipper: " + e.getMessage());
        }
    }
}
