package com.haven.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haven.dto.OneProductDetailsDTO;
import com.haven.dto.ProductDetailsDTO;
import com.haven.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/customer/products")
@Tag(name = "Product customer api", description = "Xem sản phẩm cho customer")
public class ProductCustomerController {
	
	@Autowired
    private ProductService productService;
	
    @GetMapping
    @Operation(
		    summary = "Xem tất cả sản phẩm (chỉ những shop có gói thành viên)"
		)
    public ResponseEntity<?> getActiveProducts() {
        List<ProductDetailsDTO> products = productService.getProductsFromActiveSubscribedShops();

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Không có sản phẩm nào"));
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @GetMapping("/details/{productId}")
    @Operation(
		    summary = "Xem chi tiết sản phẩm"
		)
    public OneProductDetailsDTO getProductDetailsWithSalesById(@PathVariable Integer productId) {
        return productService.getProductDetailsWithSalesById(productId);
    }
    
   
}
