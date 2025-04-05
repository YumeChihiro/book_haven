package com.haven.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.ProductSearchDTO;
import com.haven.dto.SearchResponseDTO;
import com.haven.dto.ShopDetailsDTO;
import com.haven.securities.JwtUtil;
import com.haven.service.SearchService;
import com.haven.service.ShopDetailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/customer")
@Tag(name = "Search customer api", description = "Tìm kiếm shop, sản phẩm cho customer")
@SecurityRequirement(name = "JWT")
public class SearchController {

	@Autowired
	private ShopDetailService shopService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping("/detail/{managerId}")
	@Operation(
		    summary = "Lấy thông tin chi tiết của shop: danh sách, số lượng hiện có và đã bán của sản phẩm. Các đánh giá từ người dùng"
		)
	public ResponseEntity<ShopDetailsDTO> getShopDetails(@PathVariable Integer managerId) {
		ShopDetailsDTO shopDetails = shopService.getShopDetails(managerId);
		return ResponseEntity.ok(shopDetails);
	}

	@GetMapping("/product/search")
	@Operation(
		    summary = "Tìm kiếm sản phẩm"
		)
	public ResponseEntity<SearchResponseDTO> search(@RequestParam String keyword, HttpServletRequest request) {
		Integer customerId = getCustomerIdFromToken(request);
		SearchResponseDTO response = searchService.search(keyword, customerId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/products/category/{categoryId}")
	@Operation(
		    summary = "Tìm kiếm sản phẩm theo loại"
		)
    public ResponseEntity<List<ProductSearchDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<ProductSearchDTO> products = searchService.findProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/author/{authorId}")
    @Operation(
		    summary = "Tìm kiếm sản phẩm theo tác giả"
		)
    public ResponseEntity<List<ProductSearchDTO>> getProductsByAuthor(@PathVariable Integer authorId) {
        List<ProductSearchDTO> products = searchService.findProductsByAuthor(authorId);
        return ResponseEntity.ok(products);
    }

	private Integer getCustomerIdFromToken(HttpServletRequest request) {
		// Lấy token từ header Authorization
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return null; // Không có token hoặc token không hợp lệ
		}

		String token = authHeader.substring(7); // Bỏ phần "Bearer " để lấy token
		try {
			// Trích xuất customerId từ token bằng JwtUtil
			return jwtUtil.extractUserId(token);
		} catch (Exception e) {
			// Xử lý lỗi nếu token không hợp lệ (hết hạn, sai định dạng, v.v.)
			return null;
		}
	}
}
